<template>
  <n-card>
    <n-form label-placement="left" size="medium" label-width="100">
      <n-grid :x-gap="12" :y-gap="8" item-responsive :cols="8">
        <n-gi span=3>
          <n-upload action="http://127.0.0.1:8099/saito/upload/image" @finish="handleFinish">
            <n-button v-if="showOrNot()" ghost type="primary" style="width: 105px; height: 40px; margin: 5px">上传图片</n-button>
          </n-upload>
          <n-button type="primary" style="width: 105px; height: 40px; margin: 5px" @click="saveFormData">保存</n-button>    
          <div style="margin: 0 auto">
              <n-image object-fit="cover" fallback-src="http://127.0.0.1:8099/saito/image/failed.jpg" round :src="'http://127.0.0.1:8099/saito/image/' + model.image_name" height="490" width="500"/>
          </div>
        </n-gi>
        <n-gi span=5>
          <n-form-item>
            <n-card ontent-style hoverable title="特征值（直方信息）" >
              <LineChart :chartData="checkData" />
            </n-card>
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>
  </n-card>
</template>

<script>
import { useDialog, useMessage } from 'naive-ui'
import { defineComponent } from 'vue'
import { LineChart } from "vue-chart-3"
import { Chart, registerables } from "chart.js"
Chart.register(...registerables)
export default defineComponent({
    setup() {
      const message = useMessage()
      const dialog = useDialog()
      return {
        message,
        dialog
      }
    },
    props: {
      data : {
      },
      type: String,
      red: Array,
      green: Array,
      blue: Array
    },
    components: {
      LineChart
    },
    data() {
      return {
        checkData: {
          labels: Array.from({length: 256},(v, k) => k),
          datasets: [
            {
              fill: 'origin',
              label: 'Red 通道',
              data: this.red,
              borderColor: 'rgb(255, 120, 120)',
              tension: 0,
              borderWidth: 2,
              backgroundColor: 'rgba(255, 130, 140, 0.8)',
              pointRadius: 0
            },
            {
              fill: 'origin',
              label: 'Green 通道',
              data: this.green,
              borderColor: 'rgb(120, 240, 120)',
              tension: 0.5,
              borderWidth: 2,
              backgroundColor: 'rgba(140, 255, 140, 0.8)',
              pointRadius: 0
            },
            {
              fill: 'origin',
              label: 'Blue 通道',
              data: this.blue,
              borderColor: 'rgb(75, 192, 192)',
              tension: 0.5,
              borderWidth: 2,
              backgroundColor: 'rgba(75, 192, 192, 0.8)',
              pointRadius: 0
            }
          ]
        },
        model: this.data,
      }
    },
    mounted() {
    },
    methods: {
      showOrNot() {
        if (this.type == 'save') {
          return true
        } else return false
      },
      handleFinish(event) {
        let result = JSON.parse((event.event.target).response)
        let data = result.result
        if (data.code == 1) {
          this.message.success(data.msg)
          this.model.image_name = result.file_name
        } else 
          this.message.error(data.msg)
      },
      saveFormData() {
        let image_name = this.model.image_name
        if (this.type == 'edit') {
          this.dialog.warning({
            title: '警告',
            content: '确认修改吗',
            positiveText: '是的',
            negativeText: '不确定',
            onPositiveClick: () => {
              this.postData(image_name)
              this.loading = false
            },
            onNegativeClick: () => {
              this.loading = false
            }
          })
        } else this.postData(image_name)
      },
      postData(image_name) {
        this.$axios.post('image-info/' + this.type, image_name).then(result => {
          let data = result.data
          if (data.code == 1)
            this.message.success(data.msg)
          else this.message.error(data.msg)
        })
      }
    }
})
</script>
