<template>
    <n-form>
        <n-form-item>
            <n-input></n-input>
        </n-form-item>
    </n-form>
</template>

<script>

import { useMessage } from 'naive-ui'
import { defineComponent } from 'vue'

export default defineComponent({

    name: 'LouiseConfig',

    setup() {
        const message = useMessage()
        return {
            message
        }
    },
    mounted() {
        this.message.info("What the Fuck?")
        this.$axios.post('sys-config/louise').then(result => {
            let data = result.data.datas[0].configid
            this.message.info(data)

        })
    },
    data() {
        return {
            msg: 'Hello'
        }
    }

})

</script>

<style>

</style>