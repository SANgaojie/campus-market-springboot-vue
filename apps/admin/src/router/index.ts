/**
 * 路由与模块入口
 *
 * @author 阿德
 * @date 2026/05/12
 */
import { createRouter, createWebHistory } from 'vue-router'
import { adminSession } from '@/api/session'
import DashboardView from '@/views/DashboardView.vue'
import GoodsManageView from '@/views/GoodsManageView.vue'
import OrderManageView from '@/views/OrderManageView.vue'
import UserManageView from '@/views/UserManageView.vue'
import CategoryManageView from '@/views/CategoryManageView.vue'
import CommentManageView from '@/views/CommentManageView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/', name: 'dashboard', component: DashboardView, meta: { requiresAdmin: true } },
    { path: '/goods', name: 'goods', component: GoodsManageView, meta: { requiresAdmin: true } },
    { path: '/orders', name: 'orders', component: OrderManageView, meta: { requiresAdmin: true } },
    { path: '/users', name: 'users', component: UserManageView, meta: { requiresAdmin: true } },
    { path: '/categories', name: 'categories', component: CategoryManageView, meta: { requiresAdmin: true } },
    { path: '/comments', name: 'comments', component: CommentManageView, meta: { requiresAdmin: true } },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin && (!adminSession.isLoggedIn || !adminSession.isAdmin)) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
