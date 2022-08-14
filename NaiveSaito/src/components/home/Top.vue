<template>
  <div style="width: 200px; margin: 15px 0 15px 30px">
    <img style="width: 60px;" alt="Vue logo" src="../../assets/logo.png">
    <span>MyLouise V1.6</span>
  </div>
</template>

<script>
import { defineComponent, h, ref } from 'vue'
import { NIcon } from 'naive-ui'

export default defineComponent({
  setup () {
    return {
      activeKey: ref(null),
      menuOptions
    }
  },
})

function renderIcon (icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: () =>
      h(
        'a',
        {
          href: 'https://baike.baidu.com/item/%E4%B8%94%E5%90%AC%E9%A3%8E%E5%90%9F',
          target: '_blank',
          rel: 'noopenner noreferrer'
        },
        '首页'
      ),
    key: 'hear-the-wind-sing',
  },
  {
    label: '文档',
    key: 'pinball-1973',
  },
  {
    label: '控制台',
    key: 'a-wild-sheep-chase',
  }
]

</script>

<style>

</style>