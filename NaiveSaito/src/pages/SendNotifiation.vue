<template>
  <div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        发送公告
    <span style="font-weight: 200; font-size: 16px">(Send Notifiaction)</span>
    <n-popover trigger="click">
      <template #trigger>
        <n-button text style="font-size: 26px">
          <n-icon>
            <HelpIcon /> 
          </n-icon>
        </n-button>
      </template>
      <p>利用Bot账号选择向群或者用户发送消息</p>
    </n-popover>
    </n-h1>
  </div>
  <n-divider />
  <n-alert title="Warning" type="warning" style="margin-bottom: 20px">
    <p>如果Bot账号所在群并非群管理员或者不具备发送公告的权限，发送公告类通知会失败</p>
  </n-alert>
  <n-grid :x-gap="12" :y-gap="8" :cols="12" item-responsive responsive="screen">
    <n-gi span="12 m:12 l:4">
      <n-card title="选择目标以及公告类型">
        <n-radio-group v-model:value="notifiation.type" style="margin-bottom: 20px">
          <n-radio value=1>公告类</n-radio>
          <n-radio value=0>文本类</n-radio>
        </n-radio-group>
        <n-data-table :columns="columns" :data="dataList" :pagination="pagination" :row-key="row => row.group_id" @update:checked-row-keys="handleCheck" />
      </n-card>
    </n-gi>
    <n-gi span="12 m:12 l:4">
      <n-card title="公告文本">
        <n-input v-model:value="notifiation.msg" type="textarea" placeholder="请输入你需要发送的文本"/>
      </n-card>
    </n-gi>
  </n-grid>
</template>

<script>
import { useMessage } from 'naive-ui'
import { defineComponent, reactive, ref } from 'vue'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'

const creatColumns = ( {popMessage} ) => {

    return [
    {
        type: 'selection',
    },
    {
        key: 'tag',
        value: false,
        width: 0
    },
    {
        title: '群号',
        key: 'group_id',
        ellipsis: true
    },
    {
        title: '群名',
        key: 'group_name',
        ellipsis: true
    },
    {
        title: '群备注',
        key: 'group_memo',
        ellipsis: true
    },
    {
        title: '群成员数量',
        key: 'member_count',
        ellipsis: true
    },
    ]
}

export default defineComponent({
    setup() {
        const checkedRowKeysRef = ref([])
        const message = useMessage()
        const paginationReactive = reactive({
                page: 1,
                pageSize: 15,
                showSizePicker: true,
                pageSizes: [10, 15, 25],
                onChange: (page) => {
                    paginationReactive.page = page
            },
                onPageSizeChange: (pageSize) => {
                    paginationReactive.pageSize = pageSize
                    paginationReactive.page = 1
            }
        })
        return {
            showModal: ref(false),
            pagination: paginationReactive,
            columns: creatColumns({
                popMessage (msg, type) {
                    if(type == 1) {
                        message.success(msg)
                    } else {
                        message.warning(msg)
                    }
                    
                }
            }),
            checkedRowKeys: checkedRowKeysRef,
            handleCheck(rowKeys) {
                checkedRowKeysRef.value = rowKeys
            }
        }
        
    },
    mounted() {
        this.$axios.post('group/findBy').then(result => {
        this.dataList = result.data.datas
        })
    },
    data() {
        return {
            dataList: [],
            notifiation: {
                msg: '',
                type: 1
            }
        }
    },
    components: {
        AlertIcon,
        HelpIcon,
        useMessage
    },
})
</script>
