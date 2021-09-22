import { createRouter, createWebHashHistory } from "vue-router"
 
const routes = [
    {
        path: '/home',
        name: "Home",
        component: () => import('../pages/Home.vue'),
        children: [
            {
                path: 'index',
                component: () => import('../components/home/Header.vue')
            },
            {
                path: 'config-info',
                component: () => import('../pages/ConfigInfo.vue')
            },
            {
                path: 'user-manage',
                component: () => import('../pages/louise/UserManagement.vue')
            } 
        ]
    },
    {
        path: '/',
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