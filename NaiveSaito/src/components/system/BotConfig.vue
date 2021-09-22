<template>
    <n-divider title-placement="left" >基础配置</n-divider>
    <n-form>
        <n-form-item :label="config.info" v-for="config in configs" :key="config.config_id">
            <n-input :value="config.config_value"></n-input>
        </n-form-item>
    </n-form>
    <n-button type="primary" style="margin-left: 0; width: 100px">修改</n-button>
    <n-button type="warning" ghost style="margin-left: 0; width: 100px">重置</n-button>
</template>

<script>

import { useMessage } from 'naive-ui'
import { defineComponent } from 'vue'

export default defineComponent({

    name: 'BotConfig',

    setup() {
        const message = useMessage()
        return {
            message
        }
    },
    mounted() {
        this.$axios.post('sys-config/bot').then(result => {
            
            this.configs = result.data.datas

        })
    },
    data() {
        return {
            configs: ''
        }
    }

})

</script>

<style>

</style>