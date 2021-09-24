<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        用户管理
    <span style="font-weight: 200; font-size: 16px">(User management)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>可以对QQ用户进行一些基础管理</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-card title="用户列表">
    <n-data-table :columns="columns" :data="userList" :pagination="pagination" :row-key="row => row.user_id" @update:checked-row-keys="handleCheck" />
</n-card>
</template>

<script>
import { defineComponent, reactive, h, ref } from 'vue'
import { router } from '../../router'
import { NButton, useMessage } from 'naive-ui'
import UserCard from '../../components/UserCard.vue'
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
                UserCard,
                {
                    avatar: rowData.avatar,
                    credit: rowData.credit,
                    credit_buff: rowData.credit_buff,
                    invoke_count: rowData.count_setu
                }
            )
        }
    },
    {
        title: 'QQ',
        key: 'user_id',
        width: 200,
        ellipsis: true
    },
    {
        title: '昵称',
        key: 'nickname',
        width: 300,
        ellipsis: true
    },
    {
        title: '创建时间',
        key: 'create_time',
        ellipsis: true
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
                    type: row.isEnabled == 1 ? 'primary' : 'error',
                    ghost: row.isEnabled == 1 ? true : false,
                    onClick() {
                        
                        axios.post('user/switchStatus', row).then(result => {
                            let msg = result.data.msg
                            //let code = result.data.code
                            row.isEnabled = -row.isEnabled
                            popMessage(msg, row.isEnabled)
                        })
                    }
                },
                {
                    default: () => row.isEnabled == 1 ? '良好' : '禁用'
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
                pageSize: 5,
                showSizePicker: true,
                pageSizes: [3, 5, 7],
                onChange: (page) => {
                    paginationReactive.page = page
            },
                onPageSizeChange: (pageSize) => {
                    paginationReactive.pageSize = pageSize
                    paginationReactive.page = 1
            }
        })
        return {
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
            userList: []
        }
    },
    mounted() {
        this.$axios.post('user/findAll').then(result => {
        this.userList = result.data.datas
        })
    },
    components: {
        AlertIcon,
        HelpIcon,
    },
    methods: {
    }
})
</script>

<style>
</style>