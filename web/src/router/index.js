import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/quickstart',
    name: 'QuickStart',
    component: () => import('../views/QuickStart.vue')
  },
  {
    path: '/api',
    name: 'Api',
    component: () => import('../views/Api.vue')
  },
  {
    path: '/config',
    name: 'Config',
    component: () => import('../views/Config.vue')
  },
  {
    path: '/examples',
    name: 'Examples',
    component: () => import('../views/Examples.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
