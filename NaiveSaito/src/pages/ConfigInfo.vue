<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        配置信息
    <span style="font-weight: 200; font-size: 16px">(Config Info)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>这里是一些系统允许直接进行修改的参数</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-card title="配置中心">
    <n-tabs>
        <n-tab-pane name="louise-config" tab="Louise配置项">
            <LouiseConfig></LouiseConfig>
        </n-tab-pane>
        <n-tab-pane name="saito-config" tab="Saito配置项">
            <SaitoConfig></SaitoConfig>
        </n-tab-pane>
    </n-tabs>
</n-card>
</template>

<script>
    import { defineComponent } from 'vue'
    import LouiseConfig from '../components/system/LouiseConfig.vue'
    import SaitoConfig from '../components/system/SaitoConfig.vue'
    import { router } from '../router'
    import {
        AlertCircleOutline as AlertIcon,
        HelpCircleOutline as HelpIcon
    } from '@vicons/ionicons5'

    export default defineComponent({
        name: 'ConfigInfoPage',
        components: {
            AlertIcon,
            HelpIcon,
            LouiseConfig,
            SaitoConfig
        }

    })
    
</script>

<style>

</style>