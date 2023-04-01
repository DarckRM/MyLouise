<template>
  <n-card>
    <n-form label-placement="left" size="medium" label-width="100">
      <n-grid :x-gap="12" :y-gap="8" item-responsive :cols="5">
        <n-gi span="1">
          <div style="margin: 0 auto">
            <n-image
              object-fit="cover"
              round
              :src="'https://q1.qlogo.cn/g?b=qq&nk=' + model.user_id + '&s=640'"
              width="190"
              height="190"
              style="border-radius: 200px"
            />
          </div>
        </n-gi>
        <n-gi span="2">
          <n-form-item label="用户QQ">
            <n-input placeholder="请输入用户QQ" v-model:value="model.user_id" />
          </n-form-item>
          <n-form-item label="昵称">
            <n-input placeholder="请输入用户昵称" v-model:value="model.nickname" />
          </n-form-item>
          <n-form-item label="所属群聊">
            <n-input placeholder="请输入所属群聊" v-model:value="model.group_id" />
          </n-form-item>
          <n-form-item label="QQ创建日期">
            <n-input placeholder="QQ创建的日期" v-model:value="model.join_time" />
          </n-form-item>
          <n-form-item label="操作">
            <n-button
              type="primary"
              style="width: 80px; height: 40px"
              @click="saveFormData"
              >保存</n-button
            >
            <n-button
              ghost
              type="error"
              style="width: 80px; height: 40px; margin-left: 30px"
              @click="saveFormData"
              >删除</n-button
            >
          </n-form-item>
        </n-gi>
        <n-gi span="2">
          <n-form-item label="加入日期">
            <n-input
              placeholder="请输入加入到MyLouise的日期"
              v-model:value="model.create_time"
            />
          </n-form-item>
          <n-form-item label="剩余CREDIT">
            <n-input-number
              placeholder="请输入剩余的CREDIT"
              v-model:value="model.credit"
            />
          </n-form-item>
          <n-form-item label="功能总请求次数">
            <n-input-number placeholder="功能总请求数" v-model:value="model.count_setu" />
          </n-form-item>
          <n-form-item label="CREDIT BUFF">
            <n-input-number v-model:value="model.credit_buff" size="medium" />
          </n-form-item>
          <n-form-item label="角色等级">
            <n-select
              v-model:value="model.role_id"
              placeholder="请选择角色等级"
              :options="roleOptions"
            />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { useMessage } from "naive-ui";
import { defineComponent } from "vue";

export default defineComponent({
  setup() {
    const message = useMessage();
    return {
      message,
    };
  },
  props: {
    data: {},
    type: String,
  },
  data() {
    return {
      model: this.data,
      roleOptions: this.data.roles.map((v) => ({
        label: v.role_name,
        value: v.role_id,
      })),
    };
  },
  methods: {
    saveFormData() {
      let formData = {
        user_id: this.model.user_id,
        nickname: this.model.nickname,
        group_id: this.model.group_id,
        join_time: this.model.join_time,
        create_time: this.model.create_time,
        credit: this.model.credit,
        count_setu: this.model.count_setu,
        credit_buff: this.model.credit_buff,
        role_id: this.model.role_id,
      };
      this.$axios.post("user/" + this.type, formData).then((result) => {
        let msg = result.data.msg;
        if (result.data.code == 200) {
          this.message.success(msg);
        } else {
          this.message.error(msg);
        }
      });
    },
  },
});
</script>
