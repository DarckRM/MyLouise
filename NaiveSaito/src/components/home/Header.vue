<template>
<WebSocket></WebSocket>
</template>

<script>
import { defineComponent } from 'vue'
import WebSocket from '../websocket/WebSocket.vue'

export default defineComponent({
  components: {
    WebSocket
  },
  setup () {
  }
})
</script>