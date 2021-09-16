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
      defaultExpandedKeys: ['saito-sys-config', 'louise-sys-config'],
    }
  }
})

function renderIcon (icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: 'Louise系统配置',
    key: 'louise-sys-config',
    icon: renderIcon(CogIcon),
    children: [
        {
            type: 'group',
            label: '基础项',
            key: 'louise-basic',
            children: [
                {

                }
            ]
        }
    ]
  },
  {
    label: 'Saito系统配置   ',
    key: 'saito-sys-config',
    icon: renderIcon(BookIcon),
    children: [
      {
        type: 'group',
        label: '基础项',
        key: 'people',
        children: [
          {
            label: '叙事者',
            key: 'narrator',
            icon: renderIcon(PersonIcon)
          },
          {
            label: '羊男',
            key: 'sheep-man',
            icon: renderIcon(PersonIcon)
          }
        ]
      },
      {
        label: '饮品',
        key: 'beverage',
        icon: renderIcon(WineIcon),
        children: [
          {
            label: '威士忌',
            key: 'whisky'
          }
        ]
      },
      {
        label: '过去增多，未来减少',
        key: 'the-past-increases-the-future-recedes'
      }
    ]
  }
]

</script>

<style>

</style>