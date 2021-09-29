<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        系统总览
    <span style="font-weight: 200; font-size: 16px">(System overview)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>此页面展示系统的一些总结性数据</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-card title="运行状态" style="margin-bottom: 40px">
  <n-grid x-gap="30" :cols="3">
    <n-gi>
      <n-card content-style hoverable title="CQ-HTTP 机器人">
          <div>
            <n-text italic>基于Mirai以及OneBot标准实现的qq机器人</n-text>
          </div>
          <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3><n-text style="margin-left: 10px">{{nowTime}}</n-text>
          <n-h3 prefix="bar">运行中...</n-h3>
          <n-divider />
          <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
          <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
      </n-card>
    </n-gi>
    <n-gi>
        <n-card hoverable title="YUki 网关">
            <div>
                <n-text italic>由好友Remid开发的，基于Go的网关，实现Bot请求转发</n-text>
            </div>
            <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3><n-text style="margin-left: 10px">{{nowTime}}</n-text>
            <n-h3 prefix="bar">运行中...</n-h3>
            <n-divider />
            <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
            <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
        </n-card>
    </n-gi>
    <n-gi>
        <n-card hoverable title="Louise 后台服务">
            <div>
                <n-text italic>系统后台服务</n-text>
            </div>
            <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3><n-text style="margin-left: 10px">{{ nowTime }}</n-text>
            <n-h3 prefix="bar" :type="louiseStatus">{{louiseText}}</n-h3>
          <n-divider />
            <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
            <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
        </n-card>
    </n-gi>
  </n-grid>
</n-card>
<WebSocket ref="webSocket" client_name="status_conn"></WebSocket>
</template>

<script>
import { defineComponent } from "vue"
import WebSocket from '../components/websocket/WebSocket.vue'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'

    export default defineComponent({
        name: 'StatusPanel',
        components: {
            WebSocket,
            HelpIcon
        },
        mounted() {
            this.nowTimes()
        },
        unmounted() {
          console.log("清除计时器")
          this.clear()
        },
        data() {
            return {
                nowTime: '',
                louiseStatus: 'error',
                louiseText: '未知'
            }
        },
        methods: {
            nowTimes(){
                this.nowTime = this.$refs.webSocket.louiseBootTime
                if(this.$refs.webSocket.isConn) {
                    this.louiseStatus = 'success'
                    this.louiseText = '运行良好'
                }
                else {
                    this.louiseStatus = 'error'
                    this.louiseText = '停机中'
                } 
                setInterval(this.nowTimes,5000)
                this.clear()
            },
            clear(){
                this.nowTimes = null
                clearInterval(this.nowTimes)
            }
        }
    })
</script>