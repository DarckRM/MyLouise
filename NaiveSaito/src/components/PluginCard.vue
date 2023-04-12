<template>
  <n-card>
    <n-form label-placement="left" size="medium" label-width="100">
      <n-grid x-gap="16" :cols="5">
        <n-gi span="1">
          <n-upload
            action="http://127.0.0.1:8099/saito/upload/plugin"
            list-type="file"
            @finish="handleFinish"
          >
            <n-upload-dragger>
              <div style="margin-bottom: 12px">
                <n-icon size="48" :depth="3">
                  <archive-icon />
                </n-icon>
              </div>
              <n-text style="font-size: 16px"> 点击或者拖动文件到该区域来上传 </n-text>
              <n-p depth="3" style="margin: 8px 0 0 0"> 只支持 .jar 文件 </n-p>
            </n-upload-dragger>
          </n-upload>
        </n-gi>
        <n-gi span="2">
          <n-form-item label="插件名称">
            <n-input placeholder="请输入插件名称" v-model:value="model.name" />
          </n-form-item>
          <n-form-item label="触发正则">
            <n-input placeholder="请输入触发正则" v-model:value="model.cmd"> </n-input>
          </n-form-item>
          <n-form-item label="开发者">
            <n-input placeholder="插件作者" v-model:value="model.author"> </n-input>
          </n-form-item>
          <n-form-item label="插件路径">
            <n-input disabled placeholder="rua!" v-model:value="model.path"> </n-input>
          </n-form-item>
        </n-gi>
        <n-gi span="2">
          <n-form-item label="功能一句话">
            <n-input placeholder="请输入一句话" v-model:value="model.description">
            </n-input>
          </n-form-item>
          <n-form-item label="详细说明">
            <n-input
              placeholder="Textarea"
              v-model:value="model.info"
              type="textarea"
              :autosize="{
                minRows: 3,
                maxRows: 5,
              }"
            />
          </n-form-item>
          <n-form-item label="触发类型">
            <n-radio default-checked :checked="model.type === 1" value="1"
              >主动型</n-radio
            >
            <n-radio :checked="model.type === 0" value="0">被动型</n-radio>
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { ArchiveOutline as ArchiveIcon } from "@vicons/ionicons5";
import { defineComponent } from "vue";
import { useMessage } from "naive-ui";

export default defineComponent({
  setup() {
    const message = useMessage();
    return {
      message,
    };
  },
  components: {
    ArchiveIcon,
  },
  props: {
    data: {},
    width: String,
    type: String,
  },
  data() {
    return {
      model: this.data,
    };
  },
  methods: {
    handleFinish(file) {
      var result = JSON.parse(file.event.currentTarget.response);
      if (result.result.code != 200) {
        this.message.error(result.result.msg);
      } else {
        this.model.path = "plugins/" + result.file_name;
        this.message.success(result.result.msg);
      }
    },
    saveFormData() {
      let formData = {
        plugin_id: this.model.plugin_id,
        name: this.model.name,
        cmd: this.model.cmd,
        path: this.model.path,
        info: this.model.info,
        description: this.model.description,
        type: this.model.type,
      };
      this.$axios.post("plugin-info/" + this.type, formData).then((result) => {
        let data = result.data;
        if (data.code == 200) {
          this.message.success(data.msg);
        } else {
          this.message.error(data.msg);
        }
      });
    },
  },
});
</script>
