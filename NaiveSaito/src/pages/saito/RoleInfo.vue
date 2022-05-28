<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        角色信息
    <span style="font-weight: 200; font-size: 16px">(Role info)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>管理系统中的角色信息</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-card title="角色列表">
    <div>
        <n-button ghost type="primary" size="large" style="margin: 0 10px 10px 0; width: 80px" @click="showModal = true">新增</n-button>
        <n-button type="error" size="large" style="margin: 0 10px 10px 0; width: 80px">删除</n-button>
    </div>
    <n-data-table :columns="columns" :data="dataList" :pagination="pagination" :row-key="row => row.role_id" @update:checked-row-keys="handleCheck" />
</n-card>
<n-modal v-model:show="showModal">
    <n-card style="width: 1100px;" title="新增角色" :bordered="false" size="huge">
        <RoleCard type="save" :data="dataList" :existFeature="empty" :featureList="featureList" width="width: 1000px"/>
    </n-card>
</n-modal>
</template>

<script>
import { defineComponent, reactive, h, ref } from 'vue'
import { router } from '../../router'
import { NButton, NTag, useMessage } from 'naive-ui'
import RoleCard from '../../components/RoleCard.vue'
import axios from '../../utils/request'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'
let featureListProp = []
const creatColumns = ({ popMessage }) => {

    return [
    {
        type: 'selection'
    },
    {
        type: 'expand',
        renderExpand: (rowData) => {
            return h(
                RoleCard,
                {
                  existFeature: rowData.featureInfoList,
                  featureList: featureListProp,
                  data: rowData,
                  hoverable: true,
                  type: 'edit'
                }
            )
        }
    },
    {
        title: '角色名称',
        key: 'role_name',
        width: 200,
        ellipsis: true
    },
    {
        title: '说明',
        key: 'info',
        width: 300,
        ellipsis: true
    },
    {
        title: '状态',
        key: 'is_enabled',
        render(row) {
          return h(
            NButton,
            {
              circle: true,
              style: 'margin: 0; width: 80px',
              type: row.is_enabled == 1 ? 'primary' : 'error',
              ghost: row.is_enabled == 1 ? true : false,
              onClick() {
                axios.post('role/switchStatus', row).then(result => {
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
      const featureList = []
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
        featureList,
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
        this.$axios.post('role/findBy').then(result => {
            this.dataList = result.data.datas
        })

        this.$axios.post('feature-info/findBy').then(result => {
          result.data.datas.forEach(element => {
            let temp = {
              label: element.feature_name,
              value: element.feature_id,
              type: 'success'
            }
            this.featureList.push(temp)
          })
        })
        featureListProp = this.featureList
    },
    components: {
        AlertIcon,
        HelpIcon,
        RoleCard
    },
    methods: {
    }
})
</script>

<style>
</style>