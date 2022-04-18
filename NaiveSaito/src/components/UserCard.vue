<template>
  <n-card>
    <n-form label-placement="left" size="medium" label-width="100">
      <n-grid :x-gap="12" :y-gap="8" item-responsive :cols="5">
        <n-gi span=1>
            <div style="margin: 0 auto">
                <n-image object-fit="cover" round :src="'https://q1.qlogo.cn/g?b=qq&nk=' + model.user_id + '&s=640'" width="200" height="200" style="border-radius: 200px;"/>
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
          <n-form-item label="QQ创建日期">
            <n-input placeholder="QQ创建的日期" v-model:value="model.join_time"/>
          </n-form-item>
        </n-gi>
        <n-gi span=2>
          <n-form-item label="加入日期">
            <n-input placeholder="请输入加入到MyLouise的日期" v-model:value="model.create_time"/>
          </n-form-item>
          <n-form-item label="剩余CREDIT">
            <n-input-number placeholder="请输入剩余的CREDIT" v-model:value="model.credit" />
          </n-form-item>
          <n-form-item label="功能总请求次数">
            <n-input-number placeholder="功能总请求数" v-model:value="model.count_setu" />
          </n-form-item>
          <n-form-item label="CREDIT BUFF">
            <n-input-number v-model:value="model.credit_buff" size="medium" />
          </n-form-item>
          <n-form-item label="角色等级">
            <n-select v-model:value="model.role_id" placeholder="请选择角色等级" :options="roleOptions" />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
    setup() {
    },
    props: {
      data : {
      },
      type: String
    },
    data() {
      return {
        model: this.data,
        roleOptions: this.data.roles.map((v) => ({
          label: v.role_name,
          value: v.role_id
        }))
      }
    },
    methods: {
      saveFormData() {

      }
    }
})
</script>
