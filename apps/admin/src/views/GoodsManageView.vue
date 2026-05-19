<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { adminOffShelfGoods, fetchAdminGoods } from '@/api/admin'
import type { Goods } from '@/api/types'
import { goodsStatusLabel } from '@/utils/display'

const goods = ref<Goods[]>([])
const error = ref('')

async function load() {
  try {
    goods.value = await fetchAdminGoods()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载商品失败'
  }
}

async function offShelf(id: number) {
  await adminOffShelfGoods(id)
  await load()
}

onMounted(load)
</script>

<template>
  <section class="card">
    <h2>商品管理</h2>
    <p class="muted">展示全量商品；管理员可强制下架非交易中商品。</p>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-list">
      <div v-for="item in goods" :key="item.id" class="row">
        <div>
          <strong>{{ item.title }}</strong>
          <p class="muted">ID: {{ item.id }} ｜ 分类: {{ item.categoryId }} ｜ 卖家: {{ item.sellerId }} ｜ ¥{{ item.price }}</p>
        </div>
        <div class="actions">
          <span class="badge">{{ goodsStatusLabel(item.status) }}</span>
          <button v-if="item.status === 'ON_SALE'" class="danger" @click="offShelf(item.id)">强制下架</button>
        </div>
      </div>
    </div>
  </section>
</template>
