<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { adminDeleteComment, fetchAdminComments } from '@/api/admin'
import type { GoodsComment } from '@/api/types'

const comments = ref<GoodsComment[]>([])
const error = ref('')

async function load() {
  error.value = ''
  try {
    comments.value = await fetchAdminComments()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载评论失败'
  }
}

async function remove(commentId: number) {
  await adminDeleteComment(commentId)
  await load()
}

onMounted(load)
</script>

<template>
  <section class="panel">
    <div class="section-title">
      <div>
        <p class="eyebrow">Comments</p>
        <h2>评论管理</h2>
      </div>
      <button class="secondary" @click="load">刷新</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>商品</th>
          <th>用户</th>
          <th>内容</th>
          <th>状态</th>
          <th>时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="comment in comments" :key="comment.id">
          <td>{{ comment.id }}</td>
          <td>#{{ comment.goodsId }}</td>
          <td>#{{ comment.userId }}</td>
          <td class="comment-content">{{ comment.content }}</td>
          <td>
            <span class="badge">{{ comment.deleted ? '已删除' : '可见' }}</span>
          </td>
          <td>{{ new Date(comment.createdAt).toLocaleString() }}</td>
          <td>
            <button v-if="!comment.deleted" class="danger" @click="remove(comment.id)">删除</button>
            <span v-else class="muted">无</span>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="comments.length === 0" class="muted">暂无评论。</p>
  </section>
</template>
