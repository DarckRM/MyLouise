import { createRouter, createWebHashHistory } from "vue-router"
import store from "../store/store"

const routes = [
  {
    path: '/:pathmatch(.*)',
    redirect: '/404'
  },
  {
      path: '/home',
      name: "Home",
      component: () => import('../pages/Home.vue'),
      meta: {
        auth: true
      },
      children: [
          {
              path: 'index',
              component: () => import('../pages/StatusPanel.vue')
          },
          {
              path: 'terminal',
              component: () => import('../pages/Terminal.vue')
          },
          {
              path: 'config-info',
              component: () => import('../pages/ConfigInfo.vue')
          },
          {
              path: 'send-notice',
              component: () => import('../pages/SendNotifiation.vue')
          },
          {
              path: 'update-panel',
              component: () => import('../pages/UpdatePanel.vue')
          },
          {
              path: 'user-manage',
              component: () => import('../pages/louise/UserManagement.vue')
          },
          {
              path: 'group-manage',
              component: () => import('../pages/louise/GroupManagement.vue')
          },
          {
            path: 'plugin-info',
            component: () => import('../pages/saito/PluginInfo.vue')
          },
          {
              path: 'feature-info',
              component: () => import('../pages/saito/FeatureInfo.vue')
          },
          {
              path: 'role-info',
              component: () => import('../pages/saito/RoleInfo.vue')
          },
          {
            path: 'cron-task',
            component: () => import('../pages/saito/CronTask.vue')
          },
          {
            path: 'image-manage',
            component: () => import('../pages/louise/ImageManagement.vue')
          }
      ]
  },
  {
      path: '/image-retrieve',
      name: 'ImageRetrieve',
      component: () => import('../components/ImageRetrieve.vue')
  },
  {
    path: '/',
    name: 'Login',
    component: () => import('../components/LoginPage.vue'),
    meta: {
      auth: false
    }
  },
  {
    path: '/403',
    name: '403 Page',
    component: () => import('../pages/httpResults/403.vue'),
    meta: {
      auth: false
    }
  },
  {
      path: '/404',
      name: '404 Page',
      component: () => import('../pages/httpResults/404.vue'),
      meta: {
        auth: false
      }
  },
  {
      path: '/500',
      name: '500 Page',
      component: () => import('../pages/httpResults/500.vue'),
      meta: {
        auth: false
      }
  }
]

export const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})
 
export default router

router.beforeEach((to, from, next) => {
  const now = new Date()
  if(now - store.state.token > 518400 && to.meta.auth) {
    store.commit('del_token', "")
    return next('/403')
  }
  if(to.meta.auth && !store.state.status) {
    return next('/403')
  } else {
    next()
  }
})
