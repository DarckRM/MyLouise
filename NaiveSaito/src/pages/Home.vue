<template>
    <div id="homeDiv" style="height: auto">
        <n-layout postion="absolute">
            <n-layout-header bordered style="padding: 0 30px; height: 80px;">
                <Top></Top>
            </n-layout-header>
            <n-layout has-sider>
                <n-layout-sider bordered content-style="padding: 16px;" collapse-mode="width" :collapsed-width="80" :width="280" show-trigger="bar">            
                    <Menu></Menu>
                </n-layout-sider>
                <n-layout content-style="padding: 24px;" :native-scrollbar="false">
                    <router-view>
                    </router-view>
                </n-layout>
            </n-layout>
        </n-layout>
    </div>

</template>

<script>
import { defineComponent } from 'vue'
import Header from '../components/home/Header.vue'
import Menu from '../components/home/Menu.vue'
import Top from '../components/home/Top.vue'

export default defineComponent({
    setup() {
        
    },
    components: {
        Header,
        Menu,
        Top
    }
})
</script>

<style>

</style>
