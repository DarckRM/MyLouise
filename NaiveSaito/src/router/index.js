import { createRouter, createWebHashHistory } from "vue-router"
 
const routes = [
    {
        path: '/hello',
        name: "Hello",
        component: () => import('../components/HelloWorld.vue')
    }
]
export const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})
 
export default router