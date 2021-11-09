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
  PeopleOutline as PeopleIcon,
  PersonOutline as PersonIcon,
  CogOutline as CogIcon,
  HardwareChipOutline as ChipIcon,
  HomeOutline as HomeIcon,
  NotificationsOutline as NoticeIcon,
  Cube as CubeIcon
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
    label: '发送公告',
    key: 'send-notice',
    icon: renderIcon(NoticeIcon)
  },
  {
    
    label: 'Louise系统',
    key: 'louise-sys',
    icon: renderIcon(CubeIcon),
    children: [
        {
            label: '个人管理',
            key: 'user-manage',
            icon: renderIcon(PersonIcon)
        },
        {
            label: '群组管理',
            key: 'group-manage',
            icon: renderIcon(PeopleIcon)
        }
    ]
  },
  {
    label: 'Saito系统   ',
    key: 'saito-sys',
    icon: renderIcon(BookIcon),
    children: [
        {
          label: '功能信息',
          key: 'feature-info',
          icon: renderIcon(CogIcon)
        },
        {
          label: '角色信息',
          key: 'role-info',
          icon: renderIcon(CogIcon)
        },
    ]
  }
]

</script>

<style>

</style>