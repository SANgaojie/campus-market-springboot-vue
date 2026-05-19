<!--
  LoginView 模块

  @author 阿德
  @date 2026/05/14
-->
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const error = ref('')
const form = reactive({
  username: '',
  password: '',
})

async function submit() {
  error.value = ''
  try {
    const session = await login(form)
    auth.setSession(session.token, session.user)
    router.push(typeof route.query.redirect === 'string' ? route.query.redirect : '/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败'
  }
}
</script>

<template>
  <section class="card">
    <h1 class="page-title">登录</h1>
    <form class="form" @submit.prevent="submit">
      <label>
        用户名
        <input v-model="form.username" required autocomplete="username" />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" required autocomplete="current-password" />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="primary" type="submit">登录</button>
      <RouterLink to="/register">还没有账号？去注册</RouterLink>
    </form>
  </section>
</template>
