<template>
  <div>
    <n-h1 prefix="bar" style="font-weight: 400; font-size: 32px">
      用户管理
      <span style="font-weight: 200; font-size: 16px">(User management)</span>
      <n-popover trigger="click">
        <template #trigger>
          <n-button text style="font-size: 26px">
            <n-icon>
              <HelpIcon />
            </n-icon>
          </n-button>
        </template>
        <p>可以对QQ用户进行一些基础管理</p>
      </n-popover>
    </n-h1>
  </div>
  <n-divider />
  <n-card title="用户列表">
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
      :row-key="(row) => row.user_id"
      @update:checked-row-keys="handleCheck"
      :scroll-x="1080"
    />
  </n-card>
  <n-modal v-model:show="showModal">
    <n-card style="width: 1100px" title="新增用户" :bordered="false" size="huge">
      <UserCard type="save" :data="empty"></UserCard>
    </n-card>
  </n-modal>
</template>

<script>
import { defineComponent, reactive, h, ref } from "vue";
import { NButton, useMessage, NTag } from "naive-ui";
import UserCard from "../../components/UserCard.vue";
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
        return h(UserCard, {
          data: rowData,
          hoverable: true,
          type: "edit",
        });
      },
      fixed: "left",
    },
    {
      title: "用户QQ",
      key: "user_id",
      width: 120,
      fixed: "left",
      ellipsis: true,
    },
    {
      title: "昵称",
      key: "nickname",
      width: 200,
      fixed: "left",
      ellipsis: true,
    },
    {
      title: "角色",
      key: "role_name",
      render(row) {
        var tag = "";
        switch (row.role_id) {
          case 1:
            tag = "info";
            break;
          case 2:
            tag = "error";
            break;
          default:
            tag = "warning";
        }
        return h(
          NTag,
          {
            type: tag,
          },
          {
            default: () => row.role_name,
          }
        );
      },
    },
    {
      title: "创建时间",
      key: "create_time",
      ellipsis: true,
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
            type: row.isEnabled == 1 ? "primary" : "error",
            ghost: row.isEnabled == 1 ? true : false,
            disabled: row.tag,
            onClick() {
              row.tag = true;
              axios.post("user/switchStatus", row).then((result) => {
                let msg = result.data.msg;
                row.isEnabled = -row.isEnabled;
                popMessage(msg, row.isEnabled);
              });
              row.tag = false;
            },
          },
          {
            default: () => (row.isEnabled == 1 ? "良好" : "禁用"),
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
      roleList: [],
      empty: [
        {
          roles: [],
        },
      ],
    };
  },
  mounted() {
    this.$axios.post("user/findAll").then((result) => {
      this.dataList = result.data.datas;
      // 获取Role列表
      this.$axios.post("role/findBy").then((result) => {
        this.roleList = result.data.datas;
        this.dataList.map((v) => {
          v.roles = this.roleList;
          this.empty.roles = this.roleList;
        });
      });
    });
  },
  components: {
    AlertIcon,
    HelpIcon,
    NTag,
    UserCard,
  },
  methods: {},
});
</script>

<style></style>
