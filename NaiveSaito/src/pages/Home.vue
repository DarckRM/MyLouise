<template>
  <div id="homeDiv" style="position: relative">
    <n-layout postion="absolute" bordered>
      <n-layout-header bordered style="padding: 0 30px">
        <Top :style="{ width:'200px', margin: iconStyle}"></Top>
      </n-layout-header>
      <n-layout-content :native-scrollbar="false">
        <div v-if="!showSideBar">
          <Menu mode="horizontal"></Menu>
        </div>
      </n-layout-content>
      <n-layout :has-sider="showSideBar">
        <n-layout-sider v-if="showSideBar" content-style="padding: 16px;" collapse-mode="width" :collapsed-width="80"
          :width="280" show-trigger="bar">
          <Menu></Menu>
        </n-layout-sider>
        <n-layout content-style="margin-top: 30px; height: 85vh" :native-scrollbar="false">
          <router-view>
          </router-view>
          <n-layout-footer bordered style="padding: 34px 54px">
            <n-descriptions label-placement="top" :column="showColumn">
              <n-descriptions-item label="资源">
                <n-button style="margin: 25px 30px 0 0" text @click="openTab('https://www.bilibili.com')">
                  <template #icon>
                    <n-icon>
                      <heart />
                    </n-icon>
                  </template>
                  设计资源
                </n-button><br />
                <n-button style="margin: 15px 30px 0 0" text @click="openTab('https://www.xicons.org/#/')">
                  <template #icon>
                    <n-icon>
                      <image-icon />
                    </n-icon>
                  </template>
                  图标库
                </n-button><br />
                <n-button style="margin: 15px 30px 0 0" text @click="openTab('https://www.naiveui.com/')">Naive UI
                </n-button>
              </n-descriptions-item>
              <n-descriptions-item label="帮助">
                <n-button style="margin: 25px 30px 0 0" text @click="openTab('https://docs.go-cqhttp.org/')">
                  <template #icon>
                    <n-icon>
                      <sparkles-outline />
                    </n-icon>
                  </template>
                  Go-Cqhttp
                </n-button><br />
                <n-button style="margin: 15px 30px 0 0" text @click="openTab('https://github.com/DarckRM/MyLouise')">
                  MyLouise</n-button><br />
                <n-button style="margin: 15px 30px 0 0" text @click="openTab('https://github.com/miRemid/yuki')">Yuki
                </n-button>
              </n-descriptions-item>
              <n-descriptions-item label="社区">
                <n-button style="margin: 25px 30px 0 0" text>
                  <template #icon>
                    <n-icon>
                      <videocam-outline />
                    </n-icon>
                  </template>
                  bilibili
                </n-button><br />
                <n-button style="margin: 0.9375rem 1.875rem 0 0" text>
                  <template #icon>
                    <n-icon>
                      <logo-discord />
                    </n-icon>
                  </template>
                  Discord
                </n-button><br />
                <n-button style="margin: 15px 30px 0 0" text @click="openTab('https://github.com/DarckRM/MyLouise')">
                  <template #icon>
                    <n-icon>
                      <logo-github />
                    </n-icon>
                  </template>
                  Github
                </n-button><br />
              </n-descriptions-item>
              <n-descriptions-item label="联系我">
                <n-button style="margin: 25px 30px 0 0" text
                  @click="openTab('https://steamcommunity.com/profiles/76561198144797930')">
                  <template #icon>
                    <n-icon>
                      <logo-steam />
                    </n-icon>
                  </template>
                  Steam
                </n-button><br />
                <n-button style="margin: 15px 30px 0 0" text>
                  <template #icon>
                    <n-icon>
                      <logo-google />
                    </n-icon>
                  </template>
                  Gmail
                </n-button><br />
              </n-descriptions-item>
            </n-descriptions>
            <n-divider />
            <div style="text-align: center">
              MyLouise Alpha 1.7 · Create By DarckLH (Lin Hai)
            </div>
          </n-layout-footer>
        </n-layout>
      </n-layout>
    </n-layout>
  </div>

</template>

<script>
import { defineComponent } from 'vue'
import Menu from '../components/home/Menu.vue'
import Top from '../components/home/Top.vue'
import { router } from '../router'

import {
  HeartOutline as Heart,
  ImageOutline as ImageIcon,
  LogoGithub,
  LogoDiscord,
  LogoGoogle,
  LogoSteam,
  VideocamOutline,
  SparklesOutline
} from '@vicons/ionicons5'


export default defineComponent({
  setup() {
  },
  components: {
    Menu,
    Top,
    Heart,
    ImageIcon,
    LogoGithub,
    LogoDiscord,
    LogoGoogle,
    LogoSteam,
    VideocamOutline,
    SparklesOutline
  },
  data() {
    return {
      windowWidth: document.documentElement.clientWidth,  //实时屏幕宽度
      windowHeight: document.documentElement.clientHeight,   //实时屏幕高度
      showSideBar: true,
      showColumn: 4,
      iconStyle: ''
    }
  },
  mounted() {
    // 判断当前设备屏幕大小
    var curWidth = document.documentElement.clientWidth
      if (curWidth <= 768) {
        this.switchSmallDisplay()
      } else {
        this.switchWideDisplay()
      }
    var that = this;
    // <!--把window.onresize事件挂在到mounted函数上-->
    window.onresize = () => {
      return (() => {
        window.fullHeight = document.documentElement.clientHeight
        window.fullWidth = document.documentElement.clientWidth
        that.windowHeight = window.fullHeight // 高
        that.windowWidth = window.fullWidth // 宽
      })()
    }
  },
  methods: {
    openTab(url) {
      window.open(url, '_blank')
    },
    switchWideDisplay() {
      this.showSideBar = true
      this.showColumn = 4
      this.iconStyle = '15px 0 15px 30px'
    },
    switchSmallDisplay() {
      this.showSideBar = false
      this.showColumn = 2
      this.iconStyle = '15px 0'
    }
  },
  // <!--在watch中监听实时宽高-->
  watch: {
    windowWidth(val) {
      if (val <= 768) {
        this.switchSmallDisplay()
      } else {
        this.switchWideDisplay()
      }
    }
  },
})
</script>

<style>
</style>
