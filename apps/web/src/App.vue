<!--
  应用入口组件

  @author 阿德
  @date 2026/05/09
-->
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">校园二手市场</RouterLink>
      <nav>
        <RouterLink to="/">商品</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/publish">发布</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/my-goods">我的商品</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/favorites">我的收藏</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/orders">我的订单</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/profile">个人中心</RouterLink>
        <RouterLink v-if="!auth.isLoggedIn" to="/login">登录</RouterLink>
        <RouterLink v-if="!auth.isLoggedIn" to="/register">注册</RouterLink>
        <button v-if="auth.isLoggedIn" class="link-button user-menu-button" :title="auth.user?.nickname" @click="logout">
          退出 <span class="nav-nickname">{{ auth.user?.nickname }}</span>
        </button>
      </nav>
    </header>

    <main>
      <RouterView />
    </main>
  </div>
</template>
