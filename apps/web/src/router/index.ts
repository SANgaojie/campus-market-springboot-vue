import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '@/views/HomeView.vue'
import GoodsDetailView from '@/views/GoodsDetailView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PublishGoodsView from '@/views/PublishGoodsView.vue'
import EditGoodsView from '@/views/EditGoodsView.vue'
import MyGoodsView from '@/views/MyGoodsView.vue'
import FavoriteGoodsView from '@/views/FavoriteGoodsView.vue'
import MyOrdersView from '@/views/MyOrdersView.vue'
import ProfileView from '@/views/ProfileView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/goods/:id', name: 'goods-detail', component: GoodsDetailView, props: true },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/publish', name: 'publish', component: PublishGoodsView, meta: { requiresAuth: true } },
    { path: '/goods/:id/edit', name: 'goods-edit', component: EditGoodsView, props: true, meta: { requiresAuth: true } },
    { path: '/my-goods', name: 'my-goods', component: MyGoodsView, meta: { requiresAuth: true } },
    { path: '/favorites', name: 'favorites', component: FavoriteGoodsView, meta: { requiresAuth: true } },
    { path: '/orders', name: 'orders', component: MyOrdersView, meta: { requiresAuth: true } },
    { path: '/profile', name: 'profile', component: ProfileView, meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
