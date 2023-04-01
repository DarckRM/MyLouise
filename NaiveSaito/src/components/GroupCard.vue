<template>
  <n-card>
    <div style="margin: 0 auto">
      <n-image
        object-fit="cover"
        round
        :src="model.avatar"
        width="200"
        height="200"
        style="border-radius: 200px"
      />
    </div>
    <div style="float: left">
      <n-button style="width: 85px; height: 40px; margin: 5px 5px">编辑</n-button>
      <n-button style="width: 85px; height: 40px; margin: 5px 5px" @click="reset"
        >重置</n-button
      >
    </div>
  </n-card>
</template>

<script>
import { defineComponent } from "vue";
import {
  CaretForward as CaretForwardIcon,
  TerminalOutline as Ternimal,
} from "@vicons/ionicons5";

export default defineComponent({
  setup() {},
  components: {
    CaretForwardIcon,
    Ternimal,
  },
  props: {
    data: {},
  },
  data() {
    return {
      model: this.data,
    };
  },
  methods: {
    reset() {
      this.model = data;
    },
  },
});
</script>
