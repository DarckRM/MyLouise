<template>
    <n-menu
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
  CogOutline as CogIcon
} from '@vicons/ionicons5'
import  { useMessage, NIcon } from 'naive-ui'

export default defineComponent({
  setup () {
    const message = useMessage()
    return {
      menuOptions,
      defaultExpandedKeys: ['louise-sys', 'saito-sys']
    }
  }
})

function renderIcon (icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: '系统配置',
    key: 'louise-config',
    icon: renderIcon(CogIcon)
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