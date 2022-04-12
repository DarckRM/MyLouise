import { createApp } from 'vue'
import router from './router'
import 'vfonts/Lato.css'
import App from './App.vue' 
import {
        create,
        NTransfer,
        NModal,
        NCard,
        NButton,
        NInput,
        NInputNumber,
        NForm,
        useMessage,
        NFormItem,
        NMessageProvider,
        NDivider,
        NStatistic,
        NGi,
        NGrid,
        NImage,
        NBreadcrumb,
        NBreadcrumbItem,
        NAvatar,
        NDropdown,
        NSpace,
        NPageHeader,
        NLayout,
        NLayoutFooter,
        NLayoutContent,
        NLayoutHeader,
        NLayoutSider,
        NMenu,
        NIcon,
        NText,
        NPopover,
        NH1,
        NH2,
        NH3,
        NH4,
        NH5,
        NTabs,
        NTabPane,
        NResult,
        NAlert,
        NDataTable,
        NSwitch,
        NRadioGroup,
        NRadio,
        NDescriptionsItem,
        NDescriptions,
        NDialog,
        NUpload,
        NUploadDragger,
        NP,
        NSelect
} from 'naive-ui'
const naive = create({
    components: [
        NTransfer,
        NModal,
        NCard,
        NButton,
        NInput,
        NInputNumber,
        NForm,
        useMessage,
        NFormItem,
        NMessageProvider,
        NDivider,
        NStatistic,
        NGi,
        NGrid,
        NImage,
        NBreadcrumb,
        NBreadcrumbItem,
        NAvatar,
        NDropdown,
        NSpace,
        NPageHeader,
        NLayout,
        NLayoutFooter,
        NLayoutContent,
        NLayoutHeader,
        NLayoutSider,
        NMenu,
        NIcon,
        NText,
        NPopover,
        NH1,
        NH2,
        NH3,
        NH4,
        NH5,
        NTabs,
        NTabPane,
        NResult,
        NAlert,
        NDataTable,
        NSwitch,
        NRadioGroup,
        NRadio,
        NDescriptionsItem,
        NDescriptions,
        NDialog,
        NUpload,
        NUploadDragger,
        NP,
        NSelect
    ]
})
const app = createApp(App)
app.use(router)
app.use(naive)
app.mount('#app')
import axios from './utils/request'
app.config.globalProperties.$axios = axios