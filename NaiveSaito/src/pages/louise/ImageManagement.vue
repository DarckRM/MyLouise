<template>
  <div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
        图像库
    <span style="font-weight: 200; font-size: 16px">(Image libary)</span>
    <n-popover trigger="click">
      <template #trigger>
        <n-button text style="font-size: 26px">
          <n-icon>
            <HelpIcon /> 
          </n-icon>
        </n-button>
      </template>
      <p>所有的可供系统进行检索的图片都在这里进行管理</p>
    </n-popover>
    </n-h1>
  </div>
  <n-divider />
  <n-card title="图像库">
    <div>
      <n-button ghost type="primary" size="large" style="margin: 0 10px 10px 0; width: 80px" @click="showModal = true">新增</n-button>
      <n-button type="error" size="large" style="margin: 0 10px 10px 0; width: 80px" @click="del">删除</n-button>
      <n-button type="error" size="large" ghost style="margin: 0 10px 10px 0; width: 170px; float: right" @click="initImageLib">重构特征数据</n-button>
    </div>
    <n-data-table :columns="columns" :data="dataList" :pagination="pagination" :row-key="row => row.hash_code" @update:checked-row-keys="handleCheck" />
  </n-card>
  <n-modal v-model:show="showModal">
      <n-card style="width: 1500px;" title="图片详情" :bordered="false" size="huge">
        <ImageInfoCard type="save" :data="empty"></ImageInfoCard>
      </n-card>
  </n-modal>
</template>

<script>
import { defineComponent, reactive, h, ref } from 'vue'
import { NImage, useDialog, useMessage } from 'naive-ui'
import {
    AlertCircleOutline as AlertIcon,
    HelpCircleOutline as HelpIcon
} from '@vicons/ionicons5'
import ImageInfoCard from '../../components/ImageInfoCard.vue'

const creatColumns = ({ popMessage }) => {

  return [{
    type: 'selection'
  },
  {
    type: 'expand',
    renderExpand: (rowData) => {
      let histogram = JSON.parse(rowData.histogram_json.replaceAll('\'', '\"'))
      let red = histogram[0]
      let green = histogram[1]
      let blue = histogram[2]
      return h(
        ImageInfoCard,
        {
          data: rowData,
          hoverable: true,
          type: 'edit',
          red: red,
          green: green,
          blue: blue
        }
      )
    }
  },
  {
    title: '图片',
    render(row) {
      return h(
        NImage,
        {
          width: 200,
          height: 120,
          src:  'http://127.0.0.1:8099/saito/image/' + row.image_name,
          'fallback-src': 'http://127.0.0.1:8099/saito/image/failed.jpg',
          'object-fit': 'cover'
        }
      )
    },
    width: 250
  },
  {
    title: 'Hash值',
    key: 'hash_code',
    width: 300,
    ellipsis: true
  },
  {
    title: '图片路径',
    key: 'image_path',
    ellipsis: true
  },
  {
    title: '图片名称',
    key: 'image_name',
    ellipsis: true
  }]
}

export default defineComponent({
  setup() {
      const checkedRowKeysRef = ref([])
      const dialog = useDialog()
      const message = useMessage()
      const paginationReactive = reactive({
              page: 1,
              pageSize: 5,
              showSizePicker: true,
              pageSizes: [5, 10, 15],
              onChange: (page) => {
                  paginationReactive.page = page
          },
              onPageSizeChange: (pageSize) => {
                  paginationReactive.pageSize = pageSize
                  paginationReactive.page = 1
          }
      })
      return {
        dialog,
        message,
        showModal: ref(false),
        pagination: paginationReactive,
        columns: creatColumns({
            popMessage (msg, type) {
                if(type == 1) {
                    message.success(msg)
                } else {
                    message.warning(msg)
                }
                
            }
        }),
        checkedRowKeys: checkedRowKeysRef,
        handleCheck(rowKeys) {
          checkedRowKeysRef.value = rowKeys
        }
      }
  },
  data() {
      return {
          dataList: [],
          roleList: [],
          empty: {}
      }
  },
  mounted() {
      this.$axios.post('/image-info/findAll').then(results => {
        let result = results.data
        if (result.code == 0) {
          this.message.error(result.msg)
          return
        }
        this.dataList = results.data.datas
      })
  },
  components: {
    AlertIcon,
    HelpIcon,
    NImage,
    ImageInfoCard,
    useMessage
  },
  methods: {
    initImageLib() {
      this.dialog.warning({
        title: '警告 危险操作',
        content: '你即将清除现存的图片索引数据库并重建！',
        positiveText: '是的',
        negativeText: '不确定',
        onPositiveClick: () => {
          this.$axios.post('/image-info/init').then(result => {
            let data = result.data
            if (data.code == 1)
              this.message.success(data.msg)
            else this.message.error(data.msg)
          })
          this.loading = false
        },
        onNegativeClick: () => {
          this.loading = false
        }
      })
    },
    del() {
      this.loading = true
      this.dialog.warning({
        title: '警告',
        content: '确认删除吗？',
        positiveText: '是的',
        negativeText: '不确定',
        onPositiveClick: () => {
          this.checkedRowKeys.forEach( rowKey => {
            this.$axios.post('/image-info/drop/' + rowKey ).then(result => {
              let data = result.data
              if (data.code == 1) {
                this.message.success(data.msg)
              }
              else this.message.error(data.msg)
            })
          })
          this.loading = false
        },
        onNegativeClick: () => {
          this.loading = false
        }
      })
    }
  }
})
</script>
