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
<n-alert title="Warning" type="warning" style="margin-bottom: 20px">
    <p>请注意，除非你非常确定修改参数意味着什么，否则请不要随意修改这里的内容</p>
</n-alert>
<n-card title="配置中心">
    <LouiseConfig></LouiseConfig>
</n-card>
</template>

<script>
    import { defineComponent } from 'vue'
    import LouiseConfig from '../components/system/LouiseConfig.vue'
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
        }

    })
    
</script>

<style>

</style>