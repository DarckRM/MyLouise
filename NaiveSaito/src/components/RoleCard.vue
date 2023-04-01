<template>
  <n-card :style="width">
    <n-form size="medium" label-width="100" :model="model">
      <n-grid x-gap="64" :cols="4">
        <n-gi span="2">
          <n-form-item label="角色名称">
            <n-input placeholder="请输入角色名称" v-model:value="model.role_name" />
          </n-form-item>
          <n-form-item label="说明">
            <n-input placeholder="请输入说明" v-model:value="model.info"> </n-input>
          </n-form-item>
          <n-button
            type="primary"
            size="large"
            style="width: 100px"
            @click="saveFormData"
          >
            保存
          </n-button>
        </n-gi>
        <n-gi span="2">
          <n-form-item label="选择功能">
            <n-select v-model:value="renderFeature" multiple :options="featureList" />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { defineComponent, h } from "vue";
import { useMessage } from "naive-ui";
import {
  CaretForward as CaretForwardIcon,
  TerminalOutline as Ternimal,
} from "@vicons/ionicons5";
import axios from "../utils/request";
import NTag from "naive-ui";

export default defineComponent({
  setup() {
    const message = useMessage();
    return {
      message,
    };
  },
  components: {
    CaretForwardIcon,
    Ternimal,
    NTag,
  },
  props: {
    data: {},
    existFeature: Array,
    featureList: Array,
    width: String,
    type: String,
  },
  data() {
    return {
      model: this.data,
      renderFeature: [],
    };
  },
  mounted() {
    this.existFeature.forEach((element) => {
      this.renderFeature.push(element.feature_id);
    });
  },
  methods: {
    saveFormData() {
      let formData = {
        role_id: this.model.role_id,
        role_name: "",
        info: "",
        featureInfoList: [],
      };

      formData.role_name = this.model.role_name;
      formData.info = this.model.info;
      formData.featureInfoList = this.renderFeature;

      this.$axios.post("role/" + this.type + "?type=1", formData).then((result) => {
        let data = result.data;
        if (data.code == 200) {
          this.message.success(data.msg);
        } else {
          this.message.destroyAll();
          this.message.warning(data.msg);
        }
      });
    },
  },
});
</script>
