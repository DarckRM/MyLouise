<template>
    <n-card :style="width">
        <n-form size="medium" label-width="100" :model="model">
            <n-grid x-gap=64 :cols="4">
                <n-gi span=2>
                    <n-form-item label="角色名称">
                        <n-input placeholder="请输入角色名称" v-model:value="model.role_name"/>
                    </n-form-item>
                    <n-form-item label="说明">
                        <n-input placeholder="请输入说明" v-model:value="model.info">
                        </n-input>
                    </n-form-item>
                    <n-button type="primary" size="large" style="width: 100px" @click="saveFormData">
                        保存
                    </n-button>
                </n-gi>
                <n-gi span=2>
                    <n-form-item>
                        <n-transfer
                            ref="transfer"
                            v-model:value="value"
                            :options="options"
                            filterable
                            source-title="所有功能"
                            target-title="具有功能"
                        />
                    </n-form-item>
                </n-gi>
            </n-grid>
        </n-form>
    </n-card>
</template>

<script>
import { defineComponent } from 'vue'
import {
  CaretForward as CaretForwardIcon,
  TerminalOutline as Ternimal,
} from '@vicons/ionicons5'
import axios from '../utils/request'

export default defineComponent({
    setup() {
    },
    components: {
        CaretForwardIcon,
        Ternimal
    },
    props: {
        data: {
        },
        existFeature: Array,
        width: String,
        type: String
    },
    data() {
        return {
            model: this.data,
            options: this.resolveOption(),
            value: this.resolveValue(),
        }

    },
    mounted() {
        
    },
    methods: {
        resolveOption() {
            let temp = [{
                feature_name: '系统帮助',
                feature_id: 1
            },
            {
                feature_name: '随机图片',
                feature_id: 2
            },
            {
                feature_name: '获取个人信息',
                feature_id: 3
            },
            {
                feature_name: '请求Pixiv图片',
                feature_id: 4
            },
            {
                feature_name: '图片搜索',
                feature_id: 5
            },
            {
                feature_name: '用户注册',
                feature_id: 6
            },
            ]
            return Array.apply(null, { length: temp.length }).map((v, i) => ({
                label: temp[i].feature_name,
                value: i,
                disabled: false
            }))
        },
        resolveValue() {
            return Array.apply(null, { length: this.existFeature.length }).map((v, i) => i )
        },
        saveFormData() {

            let formData = {
                role_id: this.model.role_id,
                role_name: '',
                info: '',
                featureInfoList: []
            }
            formData.role_name = this.model.role_name
            formData.info = this.model.info
            formData.featureInfoList = this.value
            console.log(formData)
            this.$axios.post('role/' + this.type, formData).then(result => {
                let msg = result.data.msg
            })
        }
    }
})
</script>
