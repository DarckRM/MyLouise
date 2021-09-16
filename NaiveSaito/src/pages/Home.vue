<template>
    <div id="homeDiv">
        <n-layout-header bordered style="padding: 0 30px;">
            <div style="width: 200px; margin: 15px 10px">
                <img style="width: 80px;" alt="Vue logo" src="../assets/logo.png">
            </div>
            <Top style="width: 100%; height: 50px" ></Top>
        </n-layout-header>
        <n-layout has-sider>
        <n-layout-sider bordered content-style="padding: 16px;" collapse-mode="width" :collapsed-width="80" :width="280" show-trigger="bar">
            <Menu></Menu>
        </n-layout-sider>
        <n-layout-content content-style="padding: 24px;">
            <Header></Header>
        </n-layout-content>
        </n-layout>
        <n-layout-footer bordered>成府路</n-layout-footer>
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
#homeDiv {
    height: 100%;
}
</style>
