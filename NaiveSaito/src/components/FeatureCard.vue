<template>
    <n-card>
        <n-form label-placement="left" size="medium" label-width="100">
            <n-grid x-gap=16 :cols="5">
                <n-gi span=1>
                    <div style="margin: 0 auto">
                        <n-processImage object-fit="cover" round :src="model.avatar" width="200" height="200" style="border-radius: 200px;"/>
                    </div>
                    
                    <div style="float: left">
                        <n-button type="primary" style="width: 105px; height: 40px; margin: 5px 50px" @click="saveFormData">保存</n-button>
                    </div>      
                </n-gi>
                <n-gi span=2>
                    <n-form-item label="功能名称">
                        <n-input placeholder="请输入功能名称" v-model:value="model.feature_name"/>
                    </n-form-item>
                    <n-form-item label="调用命令">
                        <n-input placeholder="请输入调用命令" v-model:value="model.feature_cmd">
                        </n-input>
                    </n-form-item>
                    <n-form-item label="URL">
                        <n-input placeholder="请输入调用URL" v-model:value="model.feature_url"/>
                    </n-form-item>
                    <n-form-item label="CREDIT 消耗">
                        <n-input-number v-model:value="model.credit_cost" size="small" />
                    </n-form-item>
                </n-gi>
                <n-gi span=2>
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
                            maxRows: 5
                            }"
                        />
                    </n-form-item>
                    <n-form-item label="周期调用限制">
                        <n-input placeholder="[周期]/[次数]" v-model:value="model.invoke_limit">
                        </n-input>
                    </n-form-item>
                    <n-form-item label="功能类型">
                        <n-radio-group v-model:value="model.is_original">
                        <n-radio value=1>源自系统</n-radio>
                        <n-radio value=0>外部插件</n-radio>
                        </n-radio-group>
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
        data: {
        },
        width: String,
        type: String
    },
    data() {
        return {
            model: this.data
        }

    },
    methods: {
      saveFormData() {
        let formData = {
          feature_id: this.model.feature_id,
          feature_name: this.model.feature_name,
          feature_cmd: this.model.feature_cmd,
          feature_url: this.model.feature_url,
          info: this.model.info,
          description: this.model.description,
          invoke_limit: this.model.invoke_limit,
          is_original: this.model.is_original,
          credit_cost: this.model.credit_cost
        }
        this.$axios.post('feature-info/' + this.type, formData).then(result => {
          let msg = result.data.msg
        })
      }
    }
})
</script>
