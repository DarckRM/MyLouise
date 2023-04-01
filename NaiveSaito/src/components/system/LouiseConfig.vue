<template>
  <n-divider title-placement="left">基础配置</n-divider>
  <n-grid cols="1 620:3" :x-gap="12" :y-gap="8" item-responsive>
    <n-grid-item>
      <n-card content-style hoverable title="Louise配置项">
        <n-form>
          <n-form-item
            :label="config.info"
            v-for="config in specificConfig(0)"
            :key="config.config_id"
          >
            <n-input v-model:value="config.config_value"></n-input>
          </n-form-item>
        </n-form>
      </n-card>
    </n-grid-item>
    <n-grid-item>
      <n-card content-style hoverable title="BOT配置项">
        <n-form>
          <n-form-item
            :label="config.info"
            v-for="config in specificConfig(1)"
            :key="config.config_id"
          >
            <n-input v-model:value="config.config_value"></n-input>
          </n-form-item>
        </n-form>
      </n-card>
    </n-grid-item>
    <n-grid-item>
      <n-card hoverable title="API配置项">
        <n-form>
          <n-form-item
            :label="config.info"
            v-for="config in specificConfig(2)"
            :key="config.config_id"
          >
            <n-input v-model:value="config.config_value"></n-input>
          </n-form-item>
        </n-form>
      </n-card>
    </n-grid-item>
  </n-grid>
  <n-button @click="updateConfig" type="primary" style="margin-left: 0; width: 100px"
    >修改</n-button
  >
  <n-button type="warning" ghost style="margin-left: 0; width: 100px">重置</n-button>
</template>

<script>
import { useDialog, useMessage } from "naive-ui";
import { defineComponent } from "vue";

export default defineComponent({
  name: "LouiseConfig",

  setup() {
    const dialog = useDialog();

    const message = useMessage();
    return {
      dialog,
      message,
    };
  },
  mounted() {
    this.$axios.post("sys-config/findBy").then((result) => {
      this.configs = result.data.datas;
    });
  },
  computed: {
    specificConfig() {
      return (type) => {
        let newData = [];
        this.configs.forEach((config) => {
          if (config.type == type) {
            newData.push(config);
          }
        });
        return newData;
      };
    },
  },
  data() {
    return {
      configs: [],
    };
  },
  methods: {
    updateConfig() {
      this.dialog.warning({
        title: "警告",
        content: "确认修改吗？",
        positiveText: "是的",
        negativeText: "不确定",
        onPositiveClick: () => {
          this.saveFormData();
        },
      });
    },
    saveFormData() {
      this.$axios.post("sys-config/edit", this.configs).then((result) => {
        let msg = result.data.msg;
        this.dialog.success({
          title: "成功",
          content: msg,
        });
      });
    },
  },
});
</script>

<style></style>
