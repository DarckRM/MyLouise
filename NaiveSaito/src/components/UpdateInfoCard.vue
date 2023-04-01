<template>
  <n-grid n-grid cols="1 620:3" :x-gap="12" :y-gap="8" item-responsive>
    <n-gi :span="2">
      <n-card title="最新版本">
        <v-md-preview :text="text"></v-md-preview>
        <n-button @click="checkUpdate" type="primary" style="width: 100px"
          >检查更新</n-button
        >
        <n-button type="info" disabled ghost style="width: 100px">下载</n-button>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card title="更新记录">
        <n-timeline v-for="log in updateLogs" :key="log.filename">
          <n-timeline-item
            :type="log.type"
            :title="log.name"
            :content="log.des"
            :time="log.time"
            line-type="default"
          />
        </n-timeline>
      </n-card>
    </n-gi>
  </n-grid>
</template>

<style scoped>
.n-button {
  margin-right: 15px;
}
</style>

<script>
import { useDialog, useMessage, useNotification } from "naive-ui";
import { defineComponent } from "vue";

export default defineComponent({
  setup() {
    const dialog = useDialog();
    const message = useMessage();
    const notifiction = useNotification();
    return {
      dialog,
      message,
      notifiction,
    };
  },
  mounted() {
    this.$axios
      .get("http://storage.rmdarck.icu/louise/louise-version.json")
      .then((result) => {
        result.data.forEach((log) => {
          log.time = new Date(log.time).toLocaleDateString();
          log.type = "warning";
          this.updateLogs.push(log);
        });
        this.updateLogs[1].type = "success";
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
      updateLogs: [],
      text: "# 版本日志\n## 2021年8月9日19:00:10 V1.0\n目前已经具有的功能\n``` \nRUA",
    };
  },
  methods: {
    checkUpdate() {
      this.$axios.get("http://storage.rmdarck.icu/louise/DEVELOP.md").then((result) => {
        this.text = result.data;
      });
      this.notifiction.create({
        type: "info",
        title: "有新版本可用！" + this.updateLogs[0].filename,
        content: this.updateLogs[0].des,
        meta: "2019-5-27 15:11",
        duration: 5000,
      });
    },
    saveFormData() {
      console.log(this.configs);
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
