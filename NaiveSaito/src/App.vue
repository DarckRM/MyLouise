<script setup>
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup
</script>

<template>
<n-message-provider>
<n-dialog-provider>
<n-notification-provider>
  <router-view></router-view>
</n-notification-provider>
</n-dialog-provider>
</n-message-provider>
</template>

<style>

#app {
  font-family: v-sans, v-mono, other-fallbacks;;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: auto;
}

</style>

<script>
import { defineComponent } from 'vue'
import { NDivider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui'
import { router } from './router'

export default defineComponent({
  components: {
    NDivider,
    NMessageProvider,
    NDialogProvider,
    NNotificationProvider
  }
})
</script>

