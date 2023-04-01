<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        功能信息
    <span style="font-weight: 200; font-size: 16px">(Feature info)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>已经注册可以管理的功能</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-card title="功能列表">
    <div>
        <n-button ghost type="primary" size="large" style="margin: 0 10px 10px 0; width: 80px" @click="showModal = true">新增</n-button>
        <n-button type="error" size="large" style="margin: 0 10px 10px 0; width: 80px">删除</n-button>
    </div>
    <n-data-table :columns="columns" :scroll-x="1080" :data="dataList" :pagination="pagination" :row-key="row => row.feature_id" @update:checked-row-keys="handleCheck" />
</n-card>
<n-modal v-model:show="showModal">
    <n-card style="width: 1100px;" title="新增功能" :bordered="false" size="huge">
        <FeatureCard type="save" :data="empty" width="width: 1000px"/>
    </n-card>
</n-modal>
</template>

<script>
import { defineComponent, reactive, h, ref } from 'vue'
import { NButton, NTag, useMessage } from 'naive-ui'
import FeatureCard from '../../components/FeatureCard.vue'
import axios from '../../utils/request'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'

const creatColumns = ({ popMessage }) => {

    return [
    {
        type: 'selection'
    },
    {
        type: 'expand',
        renderExpand: (rowData) => {
            return h(
                FeatureCard,
                {
                    data: rowData,
                    type: 'edit',
                    hoverable: true
                }
            )
        }
    },
    {
        title: '功能名称',
        key: 'feature_name',
        width: 200,
        ellipsis: true
    },
    {
        title: '命令',
        key: 'feature_cmd',
        width: 150,
        ellipsis: true
    },
    {
        title: 'URL',
        key: 'feature_url',
        width: 150,
        ellipsis: true
    },
    {
        title: '描述',
        key: 'description',
        ellipsis: true
    },
    {
        title: '功能来源',
        key: 'is_original',
        render(row) {
            return h(
                NTag,
                {
                    type: row.is_original == 1 ? 'primary' : 'info'
                },
                {
                    default: () => row.is_original == 1 ? '源自系统' : '外部插件'
                }
            )
        }
    },
    {
        title: '状态',
        key: 'isEnabled',
        render(row) {
            return h(
                NButton,
                {
                    circle: true,
                    style: 'margin: 0; width: 80px',
                    type: row.is_enabled == 1 ? 'primary' : 'error',
                    ghost: row.is_enabled == 1 ? true : false,
                    onClick() {
                        
                        axios.post('feature-info/switchStatus', row).then(result => {
                            let msg = result.data.msg
                            //let code = result.data.code
                            row.is_enabled = -row.is_enabled
                            popMessage(msg, row.is_enabled)
                        })
                    }
                },
                {
                    default: () => row.is_enabled == 1 ? '良好' : '禁用'
                }
            )
        },
    }
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
  data() {
    return {
      dataList: [],
      empty: []
    }
  },
  mounted() {
      this.$axios.post('feature-info/findBy').then(result => {
      this.dataList = result.data.datas
    })
  },
  components: {
      AlertIcon,
      HelpIcon,
      FeatureCard
  },
  methods: {
  }
})
</script>

<style>
</style>