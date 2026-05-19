<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { changePassword, fetchMe, updateProfile } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const error = ref('')
const success = ref('')
const loading = ref(true)
const profileForm = reactive({ nickname: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

onMounted(async () => {
  try {
    const user = await fetchMe()
    auth.setUser(user)
    profileForm.nickname = user.nickname
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载个人资料失败'
  } finally {
    loading.value = false
  }
})

async function saveProfile() {
  error.value = ''
  success.value = ''
  try {
    const user = await updateProfile({ nickname: profileForm.nickname.trim() })
    auth.setUser(user)
    profileForm.nickname = user.nickname
    success.value = '个人资料已更新'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '保存个人资料失败'
  }
}

async function savePassword() {
  error.value = ''
  success.value = ''
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    error.value = '两次输入的新密码不一致'
    return
  }
  try {
    await changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    success.value = '密码已更新，请使用新密码登录'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '修改密码失败'
  }
}
</script>

<template>
  <section class="profile-page">
    <div class="page-header-row">
      <div>
        <h1 class="page-title">个人中心</h1>
        <p class="muted">维护账号资料和登录密码。</p>
      </div>
    </div>

    <p v-if="loading" class="muted">加载中...</p>
    <template v-else>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">{{ success }}</p>

      <section class="card profile-summary">
        <div>
          <p class="muted">用户名</p>
          <strong>{{ auth.user?.username }}</strong>
        </div>
        <div>
          <p class="muted">账号状态</p>
          <span class="badge">{{ auth.user?.status === 1 ? '正常' : '已禁用' }}</span>
        </div>
        <div>
          <p class="muted">角色</p>
          <strong>{{ auth.user?.roles.join(', ') }}</strong>
        </div>
      </section>

      <section class="card">
        <h2>修改资料</h2>
        <form class="form" @submit.prevent="saveProfile">
          <label>
            昵称
            <input v-model="profileForm.nickname" required maxlength="50" />
          </label>
          <button class="primary" type="submit">保存资料</button>
        </form>
      </section>

      <section class="card">
        <h2>修改密码</h2>
        <form class="form" @submit.prevent="savePassword">
          <label>
            原密码
            <input v-model="passwordForm.oldPassword" type="password" required />
          </label>
          <label>
            新密码
            <input v-model="passwordForm.newPassword" type="password" minlength="6" maxlength="72" required />
          </label>
          <label>
            确认新密码
            <input v-model="passwordForm.confirmPassword" type="password" minlength="6" maxlength="72" required />
          </label>
          <button class="primary" type="submit">修改密码</button>
        </form>
      </section>
    </template>
  </section>
</template>
