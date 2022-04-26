<template>
<n-card title="图像检索">
  <n-grid cols="1 1000:3" :x-gap="12" :y-gap="8" item-responsive style="align-items: center">
    <n-grid-item>
      <n-card title="上传图片" style="height: 460px">
        <n-upload action="http://127.0.0.1:8099/saito/upload/image" list-type="image" @finish="handleFinish">
          <n-upload-dragger>
            <div style="margin-bottom: 12px">
              <n-icon size="48" :depth="3">
                <archive-icon />
              </n-icon>
            </div>
            <n-text style="font-size: 16px">
              点击或者拖动文件到该区域来上传
            </n-text>
            <n-p depth="3" style="margin: 8px 0 0 0">
              请不要上传敏感数据，比如你的银行卡号和密码，信用卡号有效期和安全码
            </n-p>
          </n-upload-dragger>
        </n-upload>
        <n-select :options="retrieveMethod">
        </n-select>

        <n-button type="primary" v-on:click="startCBIR">
          开始检索
        </n-button>
      </n-card>
    </n-grid-item>
    <n-grid-item offset="1">
      <n-card title="结果" style="height: 460px">
          <n-image
            width="300"
            :src=bestResult
          />
      </n-card>
    </n-grid-item>
    <n-grid-item>
      <n-card title="直方信息距离" style="height: 460px">
        <n-grid cols="4" :x-gap="12" :y-gap="8">
          <n-grid-item v-for="image in results_hi" :key="image.name">
            <n-image width="100" :src="image.url"></n-image>
          </n-grid-item>
        </n-grid>
      </n-card>
    </n-grid-item>
        <n-grid-item>
      <n-card title="相关性偏差距离" style="height: 460px">
        <n-grid cols="4" :x-gap="12" :y-gap="8">
          <n-grid-item v-for="image in results_rd" :key="image.name">
            <n-image width="100" :src="image.url"></n-image>
          </n-grid-item>
        </n-grid>
      </n-card>
    </n-grid-item>
  </n-grid>
</n-card>

</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { ArchiveOutline as ArchiveIcon } from "@vicons/ionicons5"

export default defineComponent({
  name: 'ImageRetrieve',
    components: {
      ArchiveIcon
  },
  data() {
    return {
      retrieveMethod: [
        {
          label: "颜色识别",
          value: 0
        },
        {
          label: "纹理识别",
          value: 1
        }
      ],
      results_mk: [
      ],
      results_hi: [
      ],
      results_rd: [
      ],
      compareImage: '',
      bestResult: ''
    }
  },
  methods: {
    createThumbnailUrl (file: File): Promise<string> {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(
            'http://127.0.0.1:8099/saito/image/Image_20220421213351_3w8n.jpg'
          )
        }, 1000)
      })
    },
    handleFinish (file) {
      var result = JSON.parse(file.event.currentTarget.response)
      this.compareImage = result.file_name
      console.log(result.result.file_name)
    },
    startCBIR() {
      this.$axios.post('image-info/start_cbir', 'cache/images/upload/' + this.compareImage ).then(result => {
        let dataMk = result.data.result.data.result_mkList
        let dataHI = result.data.result.data.result_hiList
        let dataRD = result.data.result.data.result_rdList

        //this.bestResult = 'http://127.0.0.1:8099' + dataHI[0].image_path + dataHI[0].image_name

        for(var result_image in dataMk) {
          this.results_mk.push({
            name: dataMk[result_image].image_name,
           // url: 'http://127.0.0.1:8099' + dataMk[result_image].image_path + dataMk[result_image].image_name
          })
        }

        for(var result_image in dataHI) {
          this.results_hi.push({
            name: dataHI[result_image].image_name,
            //url: 'http://127.0.0.1:8099' + dataHI[result_image].image_path + dataHI[result_image].image_name
          })
        }

        for(var result_image in dataRD) {
          this.results_rd.push({
            name: dataRD[result_image].image_name,
            //url: 'http://127.0.0.1:8099' + dataRD[result_image].image_path + dataRD[result_image].image_name
          })
        }
      })
    }
  }
})
</script>
