<template>
  <n-grid
    cols="2 720:10"
    :x-gap="15"
    :y-gap="8"
    item-responsive
    style="align-items: center; margin: 80px 15px"
  >
    <n-grid-item :span="2">
      <n-card title="上传图片">
        <n-upload
          action="http://127.0.0.1:8099/saito/upload/image"
          list-type="image"
          @finish="handleFinish"
        >
          <n-upload-dragger>
            <div style="margin-bottom: 12px">
              <n-icon size="48" :depth="3">
                <archive-icon />
              </n-icon>
            </div>
            <n-text style="font-size: 16px"> 点击或者拖动文件到该区域来上传 </n-text>
            <n-p depth="3" style="margin: 8px 0 0 0">
              请不要上传敏感数据，比如你的银行卡号和密码，信用卡号有效期和安全码
            </n-p>
          </n-upload-dragger>
        </n-upload>
        <div style="margin: 15px auto; pading: 0 auto; width: 160px">
          <n-button style="width: 100%" type="primary" v-on:click="startCBIR">
            开始检索
          </n-button>
        </div>
      </n-card>
    </n-grid-item>
    <n-grid-item :span="8">
      <n-spin :show="loading">
        <n-tabs>
          <n-tab-pane name="RD" tab="直方相交">
            <n-card title="直方图相交距离">
              <n-data-table
                :columns="columns"
                :data="results_hi"
                :row-key="(row) => row.image_name"
              />
            </n-card>
          </n-tab-pane>
          <n-tab-pane name="ED" tab="欧式距离">
            <n-card title="欧式偏差距离">
              <n-data-table
                :columns="columns"
                :data="results_rd"
                :row-key="(row) => row.image_name"
              />
            </n-card>
          </n-tab-pane>
        </n-tabs>
      </n-spin>
    </n-grid-item>
  </n-grid>
</template>

<script lang="ts">
import { defineComponent, h, ref, reactive } from "vue";
import { ArchiveOutline as ArchiveIcon } from "@vicons/ionicons5";
import ImageInfoCard from "../components/ImageInfoCard.vue";
import { NImage, useDialog, useMessage } from "naive-ui";

const creatColumns = ({ popMessage }) => {
  return [
    {
      type: "selection",
    },
    {
      title: "图片",
      render(row) {
        return h(NImage, {
          width: 200,
          height: 120,
          src: "http://127.0.0.1:8099/saito/image/" + row.image_name,
          "fallback-src": "http://127.0.0.1:8099/saito/image/failed.jpg",
          "object-fit": "cover",
        });
      },
      width: 250,
    },
    {
      title: "图片路径",
      key: "image_path",
      ellipsis: true,
    },
    {
      title: "图片名称",
      key: "image_name",
      ellipsis: true,
    },
  ];
};

export default defineComponent({
  name: "ImageRetrieve",
  components: {
    ArchiveIcon,
    ImageInfoCard,
    NImage,
  },
  setup() {
    const checkedRowKeysRef = ref([]);
    const dialog = useDialog();
    const message = useMessage();
    const paginationReactive = reactive({
      page: 1,
      pageSize: 5,
      showSizePicker: true,
      pageSizes: [5, 10, 15],
      onChange: (page) => {
        paginationReactive.page = page;
      },
      onPageSizeChange: (pageSize) => {
        paginationReactive.pageSize = pageSize;
        paginationReactive.page = 1;
      },
    });
    return {
      dialog,
      message,
      showModal: ref(false),
      pagination: paginationReactive,
      columns: creatColumns({
        popMessage(msg, type) {
          if (type == 1) {
            message.success(msg);
          } else {
            message.warning(msg);
          }
        },
      }),
      checkedRowKeys: checkedRowKeysRef,
      handleCheck(rowKeys) {
        checkedRowKeysRef.value = rowKeys;
      },
    };
  },
  data() {
    return {
      retrieveMethod: [
        {
          label: "颜色识别",
          value: 0,
        },
        {
          label: "纹理识别",
          value: 1,
        },
      ],
      results_mk: [],
      results_hi: [],
      results_rd: [],
      compareImage: "",
      bestResult: "",
      loading: false,
    };
  },
  methods: {
    createThumbnailUrl(file: File): Promise<string> {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve("http://127.0.0.1:8099/saito/image/Image_20220421213351_3w8n.jpg");
        }, 1000);
      });
    },
    handleFinish(file) {
      var result = JSON.parse(file.event.currentTarget.response);
      this.compareImage = result.file_name;
    },
    startCBIR() {
      if (this.compareImage == "") {
        this.message.error("请先上传图片");
        return;
      }
      this.loading = true;
      this.$axios
        .post("image-info/start_cbir", "cache/images/" + this.compareImage)
        .then((result) => {
          let dataHI = result.data.data.result_hiList;
          let dataRD = result.data.data.result_rdList;
          this.results_hi = dataHI;
          this.results_rd = dataRD;
          this.loading = false;
        });
    },
  },
});
</script>
