<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const error = ref('')
const form = reactive({
  username: '',
  nickname: '',
  password: '',
})

async function submit() {
  error.value = ''
  try {
    const session = await register(form)
    auth.setSession(session.token, session.user)
    router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '注册失败'
  }
}
</script>

<template>
  <section class="card">
    <h1 class="page-title">注册</h1>
    <form class="form" @submit.prevent="submit">
      <label>
        用户名
        <input v-model="form.username" required minlength="3" maxlength="50" autocomplete="username" />
      </label>
      <label>
        昵称
        <input v-model="form.nickname" required maxlength="50" />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" required minlength="6" autocomplete="new-password" />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="primary" type="submit">注册并登录</button>
      <RouterLink to="/login">已有账号？去登录</RouterLink>
    </form>
  </section>
</template>
