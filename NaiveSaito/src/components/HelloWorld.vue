<script setup>
import { ref } from 'vue'

defineProps({
  msg: String
})

const count = ref(0)
</script>

<template>
  <n-button>naive-ui</n-button>
</template>

<script>
  import { defineComponent } from 'vue'
  import { NButton } from 'naive-ui'

  export default defineComponent({
    components: {
      NButton
    }
  })
</script>

<style scoped>
a {
  color: #42b983;
}
</style>
