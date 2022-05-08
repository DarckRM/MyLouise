<template>
  <div style="margin: 200px auto">
  <n-result status="403" title="403 禁止访问" description="总有些门是对你关闭的">
    <template #footer>
      <n-button v-on:click="goToLogin">登录试试</n-button>
    </template>
  </n-result>
</div>
</template>

<script>
import { defineComponent } from 'vue'
import { router } from '../../router'

export default defineComponent({
  setup() {
  },
  methods: {
    goToLogin() {
      router.push('/')
    }
  }
})
</script>
