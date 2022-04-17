<template>
<n-card title="图像检索">
  <n-grid cols="1 1000:3" :x-gap="12" :y-gap="8" item-responsive style="align-items: center">
    <n-grid-item>
      <n-card title="上传图片" style="height: 460px">
          <n-upload :create-thumbnail-url="createThumbnailUrl" list-type="image" >
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
        <n-button type="primary" style="margin: 20 100">
          开始检索
        </n-button>
      </n-card>
    </n-grid-item>
    <n-grid-item>
      <n-card title="结果" style="height: 460px">
          <n-image
            width="300"
            src="https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
          />
      </n-card>
    </n-grid-item>
    <n-grid-item>
      <n-card title="可能的" style="height: 460px">
        <n-grid cols="4" :x-gap="12" :y-gap="8">
          <n-grid-item v-for="image in results" :key="image.name">
            <n-image height="100" width="100" :src="image.url"></n-image>
          </n-grid-item>
        </n-grid>
      </n-card>
    </n-grid-item>
  </n-grid>
</n-card>

</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
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
      results: [
        {
          name: 'Rusia',
          url: 'https://s3.bmp.ovh/imgs/2022/03/04b802d258e422ae.jpg'
        },
        {
          name: 'Axios',
          url: 'https://s3.bmp.ovh/imgs/2022/03/04b802d258e422ae.jpg'
        },
        {
          name: 'Cloxia',
          url: 'https://s3.bmp.ovh/imgs/2022/03/04b802d258e422ae.jpg'
        }
      ]
    }
  },
  methods: {
    createThumbnailUrl (file: File): Promise<string> {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(
            'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'
          )
        }, 1000)
      })
    }
  }
})
</script>
