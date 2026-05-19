<script setup lang="ts">
import { useRouter } from 'vue-router'
import { adminSession } from '@/api/session'

const router = useRouter()

function logout() {
  adminSession.logout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <strong>校园二手市场</strong>
        <span>管理后台</span>
      </div>
      <nav v-if="adminSession.isAdmin">
        <RouterLink to="/">概览</RouterLink>
        <RouterLink to="/goods">商品管理</RouterLink>
        <RouterLink to="/orders">订单管理</RouterLink>
        <RouterLink to="/users">用户管理</RouterLink>
        <RouterLink to="/categories">分类管理</RouterLink>
        <RouterLink to="/comments">评论管理</RouterLink>
      </nav>
    </aside>

    <main>
      <header class="topbar">
        <div>
          <p class="eyebrow">Admin Console</p>
          <h1>校园二手市场后台</h1>
        </div>
        <div class="actions">
          <span class="badge">{{ adminSession.user?.nickname || '未登录' }}</span>
          <button v-if="adminSession.isLoggedIn" class="secondary" @click="logout">退出</button>
        </div>
      </header>
      <RouterView />
    </main>
  </div>
</template>
