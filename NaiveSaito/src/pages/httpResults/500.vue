<template>
<div style="margin: 200px auto">
  <n-result
    status="500"
    title="500 服务器错误"
    description="服务器出事儿了，就一个程序员，救不了了，自求多福吧"
  >
    <template #footer>
      <n-button>散财消灾</n-button>
    </template>
  </n-result>
</div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
    setup() {
        
    },
})
</script>
