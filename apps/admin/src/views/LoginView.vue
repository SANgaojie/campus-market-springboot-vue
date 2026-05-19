<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { adminSession } from '@/api/session'

const router = useRouter()
const route = useRoute()
const error = ref('')
const form = reactive({
  username: 'admin',
  password: 'admin123456',
})

async function submit() {
  error.value = ''
  try {
    const session = await login(form.username, form.password)
    if (!session.user.roles.includes('ROLE_ADMIN')) {
      error.value = '当前账号不是管理员'
      return
    }
    adminSession.setSession(session.token, session.user)
    router.push(typeof route.query.redirect === 'string' ? route.query.redirect : '/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败'
  }
}
</script>

<template>
  <section class="card">
    <h2>管理员登录</h2>
    <p class="muted">开发环境默认账号：admin / admin123456。上线前必须修改配置。</p>
    <form class="form" @submit.prevent="submit">
      <label>
        用户名
        <input v-model="form.username" autocomplete="username" required />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" autocomplete="current-password" required />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="primary" type="submit">登录后台</button>
    </form>
  </section>
</template>
