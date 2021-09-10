import { createApp } from 'vue'
import App from './App.vue'

createApp(App).mount('#app')

import Axios from './utils/request.js'
app.config.globalProperties.$axios = Axios
