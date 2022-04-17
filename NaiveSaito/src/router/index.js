import { createRouter, createWebHashHistory } from "vue-router"
 
const routes = [
    {
        path: '/home',
        name: "Home",
        component: () => import('../pages/Home.vue'),
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
                path: 'user-manage',
                component: () => import('../pages/louise/UserManagement.vue')
            },
            {
                path: 'group-manage',
                component: () => import('../pages/louise/GroupManagement.vue')
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
              path: 'image-manage',
              component: () => import('../pages/louise/ImageManagement.vue')
            }
        ]
    },
    {
        path: '/',
        name: 'ImageRetrieve',
        component: () => import('../components/ImageRetrieve.vue')
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../components/LoginPage.vue')
  },
    {
        path: '/404',
        name: '404 Page',
        component: () => import('../pages/httpResults/404.vue')
    },
    {
        path: '/500',
        name: '500 Page',
        component: () => import('../pages/httpResults/500.vue')
    }
]
export const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})
 
export default router