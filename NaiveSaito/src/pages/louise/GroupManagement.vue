<template>
  <div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
      群组管理
      <span style="font-weight: 200; font-size: 16px">(Group management)</span>
      <n-popover trigger="click">
        <template #trigger>
          <n-button text style="font-size: 26px">
            <n-icon>
              <HelpIcon />
            </n-icon>
          </n-button>
        </template>
        <p>对已经注册的群组进行管理</p>
      </n-popover>
    </n-h1>
  </div>
  <n-divider />
  <n-card title="功能列表">
    <div>
      <n-button
        ghost
        type="primary"
        size="large"
        style="margin: 0 10px 10px 0; width: 80px"
        @click="showModal = true"
        >新增</n-button
      >
      <n-button type="error" size="large" style="margin: 0 10px 10px 0; width: 80px"
        >删除</n-button
      >
    </div>
    <n-data-table
      :columns="columns"
      :data="dataList"
      :pagination="pagination"
      :row-key="(row) => row.group_id"
      @update:checked-row-keys="handleCheck"
      :scroll-x="1270"
    />
  </n-card>
  <n-modal v-model:show="showModal">
    <n-card style="width: 1100px" title="新增群组" :bordered="false" size="huge">
      <GroupCard type="save" :data="empty"></GroupCard>
    </n-card>
  </n-modal>
</template>

<script>
import { defineComponent, reactive, h, ref } from "vue";
import { router } from "../../router";
import { NButton, NTag, useMessage } from "naive-ui";
import GroupCard from "../../components/GroupCard.vue";
import axios from "../../utils/request";
import {
  AlertCircleOutline as AlertIcon,
  HelpCircleOutline as HelpIcon,
} from "@vicons/ionicons5";

const creatColumns = ({ popMessage }) => {
  return [
    {
      type: "selection",
    },
    {
      type: "expand",
      renderExpand: (rowData) => {
        return h(GroupCard, {
          data: rowData,
          hoverable: true,
        });
      },
      fixed: "left",
    },
    {
      key: "tag",
      value: false,
      width: 0,
    },
    {
      title: "群号",
      key: "group_id",
      width: 120,
      ellipsis: true,
      fixed: "left",
    },
    {
      title: "群名",
      key: "group_name",
      width: 200,
      ellipsis: true,
      fixed: "left",
    },
    {
      title: "角色",
      key: "role_name",
      width: 100,
      render(row) {
        return h(
          NTag,
          {
            type: row.role_id == 1 ? "primary" : "info",
          },
          {
            default: () => row.role_name,
          }
        );
      },
    },
    {
      title: "群备注",
      key: "group_memo",
      width: 260,
      ellipsis: true,
    },
    {
      title: "群成员数量",
      key: "member_count",
      ellipsis: true,
    },
    {
      title: "群等级",
      key: "group_level",
    },
    {
      title: "状态",
      key: "isEnabled",
      render(row) {
        return h(
          NButton,
          {
            circle: true,
            style: "margin: 0; width: 80px",
            type: row.is_enabled == 1 ? "primary" : "error",
            ghost: row.is_enabled == 1 ? true : false,
            disabled: row.tag,
            onClick() {
              row.tag = true;
              axios.post("group/switchStatus", row).then((result) => {
                let msg = result.data.msg;
                //let code = result.data.code
                if (result.data.code == 200) {
                  row.is_enabled = -row.is_enabled;
                }
                popMessage(msg, row.is_enabled);
              });
              row.tag = false;
            },
          },
          {
            default: () => (row.is_enabled == 1 ? "良好" : "禁用"),
          }
        );
      },
    },
  ];
};

export default defineComponent({
  setup() {
    const checkedRowKeysRef = ref([]);
    const message = useMessage();
    const paginationReactive = reactive({
      page: 1,
      pageSize: 15,
      showSizePicker: true,
      pageSizes: [10, 15, 25],
      onChange: (page) => {
        paginationReactive.page = page;
      },
      onPageSizeChange: (pageSize) => {
        paginationReactive.pageSize = pageSize;
        paginationReactive.page = 1;
      },
    });
    return {
      showModal: ref(false),
      tagA: false,
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
      dataList: [],
      empty: {},
    };
  },
  mounted() {
    this.$axios.post("group/findBy").then((result) => {
      this.dataList = result.data.datas;
    });
  },
  components: {
    AlertIcon,
    HelpIcon,
    GroupCard,
  },
  methods: {},
});
</script>

<style></style>
