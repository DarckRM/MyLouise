<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        控制台
    <span style="font-weight: 200; font-size: 16px">(Terminal)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>这里是系统的实时日志，目前存在很多BUG</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-alert title="Warning" type="warning" style="margin-bottom: 20px">
    <p>请注意，除非你非常确定修改参数意味着什么，否则请不要随意修改这里的内容</p>
</n-alert>
<n-card title="输出">
    <div v-html="terminal_output" style="height: 300px; overflow: scroll"></div>
    <WebSocket ref="webSocket" client_name="terminal_info" data=""></WebSocket>
    <n-button @click="displayLog">显示日志</n-button>
</n-card>
</template>

<script>
import { defineComponent } from 'vue'
import axios from '../utils/request'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'
import WebSocket from '../components/websocket/WebSocket.vue'

    export default defineComponent({
        name: 'ConfigInfoPage',
        components: {
            AlertIcon,
            HelpIcon,
            WebSocket
        },
        data() {
            return {
                terminal_output: ''
            }
        },
        mounted() {

            axios.get('/output_log').then(result => {
            })
        },
        methods: {
            displayLog() {
                this.terminal_output = this.$refs.webSocket.data
                setInterval(this.displayLog, 1000)
                this.clear()
            },
            clear() {
                this.displayLog = null
                clearInterval(this.displayLog)
            }
        }

    })
    
</script>

<style>

</style>