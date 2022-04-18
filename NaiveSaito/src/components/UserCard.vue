<template>
  <n-card>
    <n-form label-placement="left" size="medium" label-width="100">
      <n-grid :x-gap="12" :y-gap="8" item-responsive :cols="5">
        <n-gi span=1>
            <div style="margin: 0 auto">
                <n-image object-fit="cover" round :src="model.avatar" width="200" height="200" style="border-radius: 200px;"/>
            </div>
            <div style="float: left">
                <n-button type="primary" style="width: 105px; height: 40px; margin: 5px 50px" @click="saveFormData">保存</n-button>
            </div>      
          </n-gi>
        <n-gi span=2>
          <n-form-item label="用户QQ">
            <n-input placeholder="请输入用户QQ" v-model:value="model.user_id"/>
          </n-form-item>
          <n-form-item label="昵称">
            <n-input placeholder="请输入用户昵称" v-model:value="model.nickname"/>
          </n-form-item>
          <n-form-item label="所属群聊">
            <n-input placeholder="请输入所属群聊" v-model:value="model.group_id"/>
          </n-form-item>
        </n-gi>
        <n-gi span=2>
          <n-form-item label="加入日期">
            <n-input placeholder="请输入加入到MyLouise的日期" v-model:value="model.create_time"/>
          </n-form-item>
          <n-form-item label="CREDIT BUFF">
            <n-input-number v-model:value="value" size="large" clearable />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { defineComponent, ref } from 'vue'

export default defineComponent({
    setup() {
        return {
          value: ref(0)
        }
    },
    props: {
      data : {
      },
      type: String
    },
    data() {
      return {
        model: this.data
      }
    },
    methods: {
      saveFormData() {

      }
    }
})
</script>
