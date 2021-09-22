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
    <n-data-table :columns="columns" :data="userList" :pagination="pagination" />
</n-card>
</template>

<script>
import { defineComponent, reactive, h } from 'vue'
import { router } from '../../router'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'
import { NSwitch } from 'naive-ui'

export default defineComponent({
    setup() {
        const paginationReactive = reactive({
                page: 2,
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
        switchStatus (row) {
            row.isEnabled = !row.isEnabled
            console.log("切换咯")
        }
    }
        
    },
    data() {
        return {
            userList: [],
            columns: [
            {
                title: 'QQ',
                key: 'user_id'
            },
            {
                title: '昵称',
                key: 'nickname'
            },
            {
                title: '创建时间',
                key: 'create_time'
            },
            {
                title: '启用/禁用',
                key: 'isEnabled',
                render(row) {
                    return h(
                        NSwitch,
                        {
                            value: row.isEnabled == 1 ? true : false,
                            onUpdateValue: () => switchStatus(row)
                        }
                    )
                },
            }
            ]
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