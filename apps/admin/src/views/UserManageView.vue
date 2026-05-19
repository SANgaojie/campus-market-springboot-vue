<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { disableUser, enableUser, fetchAdminUsers } from '@/api/admin'
import type { UserProfile } from '@/api/types'

const users = ref<UserProfile[]>([])
const error = ref('')

async function load() {
  try {
    users.value = await fetchAdminUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载用户失败'
  }
}

async function enable(id: number) {
  await enableUser(id)
  await load()
}

async function disable(id: number) {
  await disableUser(id)
  await load()
}

onMounted(load)
</script>

<template>
  <section class="card">
    <h2>用户管理</h2>
    <p class="muted">当前接口返回用户角色信息；启用/禁用操作已接入。</p>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-list">
      <div v-for="user in users" :key="user.id" class="row">
        <div>
          <strong>{{ user.nickname }}</strong>
          <p class="muted">ID: {{ user.id }} ｜ {{ user.username }} ｜ {{ user.roles.join(', ') }}</p>
        </div>
        <div class="actions">
          <button class="secondary" @click="enable(user.id)">启用</button>
          <button class="danger" @click="disable(user.id)">禁用</button>
        </div>
      </div>
    </div>
  </section>
</template>
