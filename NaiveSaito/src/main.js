import { createApp } from 'vue'
import App from './App.vue' 

const app = createApp(App)
app.mount('#app')
import axios from './utils/request'
app.config.globalProperties.$axios = axios