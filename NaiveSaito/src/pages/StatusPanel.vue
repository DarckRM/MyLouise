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
    <n-grid cols="12" :x-gap="12" :y-gap="8" item-responsive responsive="screen">
      <n-gi span="12 m:12 l:4">
        <n-card content-style hoverable title="CQ-HTTP 机器人">
          <div>
            <n-text italic>基于Mirai以及OneBot标准实现的qq机器人</n-text>
          </div>
          <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3>
          <n-text style="margin-left: 10px">{{ nowTime }}</n-text>
          <n-h3 prefix="bar">运行中...</n-h3>
          <n-divider />
          <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
          <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
        </n-card>
      </n-gi>
      <n-gi span="12 m:12 l:4">
        <n-card hoverable title="YUki 网关">
          <div style="overflow: hidden">
            <n-text italic>由好友Remid开发的，基于Go的网关，实现Bot请求转发</n-text>
          </div>
          <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3>
          <n-text style="margin-left: 10px">{{ nowTime }}</n-text>
          <n-h3 prefix="bar">运行中...</n-h3>
          <n-divider />
          <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
          <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
        </n-card>
      </n-gi>
      <n-gi span="12 m:12 l:4">
        <n-card hoverable title="Louise 后台服务">
          <div>
            <n-text italic>系统后台服务</n-text>
          </div>
          <n-h3 type="info" prefix="bar" style="display: inline">运行时间</n-h3>
          <n-text style="margin-left: 10px">{{ nowTime }}</n-text>
          <n-h3 prefix="bar" :type="louiseStatus">{{ louiseText }}</n-h3>
          <n-divider />
          <n-button ghost type="primary" style="margin: 0 20px 10px 0; width: 100px">重启</n-button>
          <n-button type="error" style="margin: 0 20px 0 0; width: 100px">终结</n-button>
        </n-card>
      </n-gi>
    </n-grid>
  </n-card>
  <StatisticPanel></StatisticPanel>
  <WebSocket ref="webSocket" :client_name="client_name" data=""></WebSocket>
  <WebSocket ref="webSocket_cpuInfo" :client_name="client_name_cp" data=""></WebSocket>
</template>

<script>
import { defineComponent } from "vue"
import WebSocket from '../components/websocket/WebSocket.vue'
import axios from "../utils/request"
import {
  HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'
import StatisticPanel from "../components/StatisticPanel.vue"

export default defineComponent({
  name: 'StatusPanel',
  components: {
    WebSocket,
    HelpIcon,
    StatisticPanel
  },
  data() {
    return {
      nowTime: '',
      louiseStatus: 'error',
      louiseText: '未知',
      client_name: 'status_conn' + (new Date()).valueOf(),
      client_name_cp: 'cpu_payload' + (new Date()).valueOf(),
      nowTimes: null
    }
  },
  mounted() {
    axios.get('/saito_ws/cpu_payload/' + this.client_name_cp).then(result => {
    })
    this.nowTimes = setInterval(() => {
      if (this.$refs.webSocket.isConn) {
        this.louiseStatus = 'success'
        this.louiseText = 'CPU负载: ' + this.$refs.webSocket_cpuInfo.data.cpu_payload
      }
      else {
        this.louiseStatus = 'error'
        this.louiseText = '停机中'
      }
    }, 3000)
  },
  methods: {
    clear() {
      clearInterval(this.nowTimes)
      this.nowTimes = null
    }
  },
  watch: {
    $route() {
      axios.get('/saito_ws/stop_run_cpu_payload')
      this.clear()
    }
  }
})
</script>