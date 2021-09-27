<template>
    <n-button @click="sendDataToServer">
        确认发送
    </n-button>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
    props: {
        client_name: ''
    },
    name: 'WebSocket',
    setup(props) {
        const client_name = props.client_name
        return {
            client_name
        }
    },
    data() {
        
        return {
            //后台系统运行时间
            louiseBootTime: '未知',
            // 连接状态
            isConn: false,
            // ws是否启动
            wsIsRun: false,
            // 定义ws对象
            webSocket: null,
            // ws请求链接（类似于ws后台地址）
            ws: 'ws://127.0.0.1:8099/saito_ws/' + this.client_name,
            // ws定时器
            wsTimer: null,
        }
    },    
    async mounted() {
        this.wsIsRun = true
        this.wsInit()
    },
    methods: {
        sendDataToServer() {
            if (this.webSocket.readyState === 1) {
                this.webSocket.send('来自前端的数据')
            } else {
                throw Error('服务未连接')
            }
        },
        /**
         * 初始化ws
         */
        wsInit() {
            
            if (!this.wsIsRun) return
            // 销毁ws
            this.wsDestroy()
            // 初始化ws
            this.webSocket = new WebSocket(this.ws)
            // ws连接建立时触发
            this.webSocket.addEventListener('open', this.wsOpenHanler)
            // ws服务端给客户端推送消息
            this.webSocket.addEventListener('message', this.wsMessageHanler)
            // ws通信发生错误时触发
            this.webSocket.addEventListener('error', this.wsErrorHanler)
            // ws关闭时触发
            this.webSocket.addEventListener('close', this.wsCloseHanler)

            // 检查ws连接状态,readyState值为0表示尚未连接，1表示建立连接，2正在关闭连接，3已经关闭或无法打开
            clearInterval(this.wsTimer)
            this.wsTimer = setInterval(() => {
                if (this.webSocket.readyState === 1) {
                    this.isConn = true
                    clearInterval(this.wsTimer)
                } else {
                    this.isConn = false
                    console.log('ws建立连接失败')
                    this.wsInit()
                }
            }, 3000)
        },
        wsOpenHanler(event) {
            this.isConn = true
            console.log('ws建立连接成功')
        },
        wsMessageHanler(e) {
            const redata = JSON.parse(e.data)
            this.louiseBootTime = redata.bootTime
            console.log(this.louiseBootTime)
            //console.log(redata)
        },
        /**
         * ws通信发生错误
         */
        wsErrorHanler(event) {
            this.isConn = false
            console.log(event, '通信发生错误')
            this.wsInit()
        },
        /**
         * ws关闭
         */
        wsCloseHanler(event) {
            this.isConn = false
            console.log(event, 'ws关闭')
            this.wsInit()
        },
        /**
         * 销毁ws
         */
        wsDestroy() {
            if (this.webSocket !== null) {
                this.webSocket.removeEventListener('open', this.wsOpenHanler)
                this.webSocket.removeEventListener('message', this.wsMessageHanler)
                this.webSocket.removeEventListener('error', this.wsErrorHanler)
                this.webSocket.removeEventListener('close', this.wsCloseHanler)
                this.webSocket.close()
                this.webSocket = null
                clearInterval(this.wsTimer)
            }
        },
    }

})

</script>
