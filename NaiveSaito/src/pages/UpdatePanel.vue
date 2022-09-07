<template>
<div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        检查更新
    <span style="font-weight: 200; font-size: 16px">(Update panel)</span>
    <n-popover trigger="click">
        <template #trigger>
            <n-button text style="font-size: 26px">
                <n-icon>
                    <HelpIcon /> 
                </n-icon>
            </n-button>
        </template>
        <p>在这里你可以获取 Louise 系统的版本更新信息并且直接在线更新</p>
    </n-popover>
    </n-h1>
</div>
<n-divider />
<n-alert title="Warning" type="warning" style="margin-bottom: 20px">
    <p>请注意，此功能仍在开发测试中，可能无法正常使用，如果遇到使用问题请在 Github issue 中提出</p>
</n-alert>
<UpdateInfoCard></UpdateInfoCard>
</template>

<script>
    import { defineComponent } from 'vue'
    import UpdateInfoCard from '../components/UpdateInfoCard.vue'
    import {
        AlertCircleOutline as AlertIcon,
        HelpCircleOutline as HelpIcon
    } from '@vicons/ionicons5'

    export default defineComponent({
        name: 'ConfigInfoPage',
        components: {
            AlertIcon,
            HelpIcon,
            UpdateInfoCard,
        }

    })
    
</script>

<style>

</style>