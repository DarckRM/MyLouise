<script setup>
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup
import HelloWorld from './components/HelloWorld.vue'
import LoginPage from './components/LoginPage.vue';
</script>

<template>
<n-message-provider>
<router-view>
</router-view>
</n-message-provider>

</template>

<style>

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

</style>

<script>
import { defineComponent } from 'vue'
import { NDivider, NMessageProvider } from 'naive-ui'
  import { router } from './router'

export default defineComponent({
  components: {
    NDivider,
    NMessageProvider
  }
})
</script>

