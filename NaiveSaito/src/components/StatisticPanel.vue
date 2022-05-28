<template>
<n-card title="统计数据">
  <n-grid n-grid cols="1 620:3" :x-gap="12" :y-gap="8" item-responsive >
    <n-gi :span="2">
      <n-row>
        <n-col :span="12">
          <n-statistic label="图片数据" :value="99">
            <template #prefix>
              <n-icon>
                <save />
              </n-icon>
            </template>
            <template #suffix>
              / 100
            </template>
          </n-statistic>
        </n-col>
        <n-col :span="12">
          <n-statistic label="活跃用户">
            1,234,123
          </n-statistic>
        </n-col>
      </n-row>
    </n-gi>
    <n-gi>
      <n-card>
        <n-timeline size="large">
          <n-timeline-item content="啊" />
          <n-timeline-item type="success" title="成功" content="哪里成功" time="2018-04-03 20:46" />
          <n-timeline-item type="error" content="哪里错误" time="2018-04-03 20:46" />
          <n-timeline-item type="warning" title="警告" content="哪里警告" time="2018-04-03 20:46" />
          <n-timeline-item type="info" title="信息" content="是的" time="2018-04-03 20:46" />
        </n-timeline>
      </n-card>
    </n-gi>
  </n-grid>
</n-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { Save } from '@vicons/ionicons5'

export default defineComponent({
  components: {
    Save
  }
})
</script>