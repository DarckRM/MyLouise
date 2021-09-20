<template>
    <n-menu
    @update:value="menuClick"
    :options="menuOptions"
    :root-indent="36"
    :indent="12"
    :default-expanded-keys="defaultExpandedKeys"
    />
</template>

<script>
import { defineComponent, h } from 'vue'
import {
  BookOutline as BookIcon,
  PersonOutline as PersonIcon,
  WineOutline as WineIcon,
  CogOutline as CogIcon,
  HardwareChipOutline as ChipIcon,
  HomeOutline as HomeIcon
} from '@vicons/ionicons5'
import  { useMessage, NIcon } from 'naive-ui'
import { router } from '../../router'

export default defineComponent({
  setup () {
    const message = useMessage()
    return {
      menuOptions,
      defaultExpandedKeys: ['louise-sys', 'saito-sys'],
      menuClick(menu) {
         router.push('/home/' + menu)
      }
    }
  }
})

function renderIcon (icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: '主页',
    key: 'index',
    icon: renderIcon(HomeIcon)
  },
  {
    label: '配置信息',
    key: 'config-info',
    icon: renderIcon(ChipIcon)
  },
  {
    
    label: 'Louise系统',
    key: 'louise-sys',
    icon: renderIcon(BookIcon),
    children: [
        {
            type: 'group',
            label: '基础项',
            key: 'louise-basic',
            children: [
            ]
        }
    ]
  },
  {
    label: 'Saito系统   ',
    key: 'saito-sys',
    icon: renderIcon(BookIcon),
    children: [
      {
        type: 'group',
        label: '基础项',
        key: 'people',
        children: [
          {
            label: '插件管理',
            key: 'plugin-management',
            icon: renderIcon(CogIcon)
          },
          {
            label: '权限管理',
            key: 'power-management',
            icon: renderIcon(CogIcon)
          },
        ]
      },
    ]
  }
]

</script>

<style>

</style>