<script setup>
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup
import HelloWorld from './components/HelloWorld.vue'
import LoginPage from './components/LoginPage.vue';
</script>

<template>
<n-message-provider>
<img style="width: 150px" alt="Vue logo" src="./assets/logo.png">
MyLouise Backfront Alpha
<n-divider />
<LoginPage></LoginPage>
</n-message-provider>
</template>

<style>

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

</style>

<script>
import { defineComponent } from 'vue'
import { NDivider, NMessageProvider } from 'naive-ui'

export default defineComponent({
  components: {
    NDivider,
    NMessageProvider
  }
})
</script>

