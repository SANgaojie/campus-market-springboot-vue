<!--
  CategoryManageView 模块

  @author 阿德
  @date 2026/05/08
-->
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  createAdminCategory,
  disableAdminCategory,
  enableAdminCategory,
  fetchAdminCategories,
  updateAdminCategory,
} from '@/api/admin'
import type { Category } from '@/api/types'

const categories = ref<Category[]>([])
const error = ref('')
const editingId = ref<number | null>(null)
const form = reactive({ name: '', sortOrder: 0 })

async function load() {
  error.value = ''
  try {
    categories.value = await fetchAdminCategories()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载分类失败'
  }
}

function editCategory(category: Category) {
  editingId.value = category.id
  form.name = category.name
  form.sortOrder = category.sortOrder
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.sortOrder = 0
}

async function submit() {
  error.value = ''
  const payload = { name: form.name.trim(), sortOrder: form.sortOrder }
  try {
    if (editingId.value) {
      await updateAdminCategory(editingId.value, payload)
    } else {
      await createAdminCategory(payload)
    }
    resetForm()
    await load()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '保存分类失败'
  }
}

async function toggleCategory(category: Category) {
  if (category.enabled === 1) {
    await disableAdminCategory(category.id)
  } else {
    await enableAdminCategory(category.id)
  }
  await load()
}

onMounted(load)
</script>

<template>
  <section class="card">
    <div class="section-title">
      <div>
        <p class="eyebrow">Categories</p>
        <h2>分类管理</h2>
        <p class="muted">维护商品分类、排序和启用状态。</p>
      </div>
      <button class="secondary" @click="load">刷新</button>
    </div>

    <form class="inline-form" @submit.prevent="submit">
      <input v-model="form.name" placeholder="分类名称" maxlength="50" required />
      <input v-model.number="form.sortOrder" type="number" min="0" placeholder="排序值" required />
      <button type="submit">{{ editingId ? '保存修改' : '新增分类' }}</button>
      <button v-if="editingId" type="button" class="secondary" @click="resetForm">取消编辑</button>
    </form>

    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-list">
      <div v-for="category in categories" :key="category.id" class="row">
        <div>
          <strong>{{ category.name }}</strong>
          <p class="muted">ID: {{ category.id }} ｜ 排序: {{ category.sortOrder }}</p>
        </div>
        <div class="actions">
          <span class="badge">{{ category.enabled === 1 ? '启用' : '禁用' }}</span>
          <button class="secondary" @click="editCategory(category)">编辑</button>
          <button :class="category.enabled === 1 ? 'danger' : 'secondary'" @click="toggleCategory(category)">
            {{ category.enabled === 1 ? '禁用' : '启用' }}
          </button>
        </div>
      </div>
    </div>
    <p v-if="categories.length === 0" class="muted">暂无分类。</p>
  </section>
</template>
