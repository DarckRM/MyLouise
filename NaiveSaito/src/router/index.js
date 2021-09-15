import { createRouter, createWebHashHistory } from "vue-router"
 
const routes = [
    {
        path: '/home',
        name: "Home",
        component: () => import('../pages/Home.vue')
    },
    {
        path: '/',
        name: 'Login',
        component: () => import('../components/LoginPage.vue')
    }
]
export const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})
 
export default router