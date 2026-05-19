<!--
  MyGoodsView 模块

  @author 阿德
  @date 2026/05/16
-->
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchMyGoods, offShelfGoods, relistGoods } from '@/api/goods'
import type { Goods } from '@/api/types'
import { goodsStatusLabel } from '@/utils/display'

const goods = ref<Goods[]>([])
const error = ref('')

async function load() {
  error.value = ''
  try {
    goods.value = await fetchMyGoods()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载我的商品失败'
  }
}

async function offShelf(goodsId: number) {
  await offShelfGoods(goodsId)
  await load()
}

async function relist(goodsId: number) {
  await relistGoods(goodsId)
  await load()
}

onMounted(load)
</script>

<template>
  <section>
    <h1 class="page-title">我的商品</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-list">
      <article v-for="item in goods" :key="item.id" class="card">
        <div style="display: flex; justify-content: space-between; gap: 16px; align-items: center">
          <div>
            <span class="badge">{{ goodsStatusLabel(item.status) }}</span>
            <h3>{{ item.title }}</h3>
            <p class="price">¥{{ item.price }}</p>
          </div>
          <div class="actions">
            <RouterLink class="secondary" :to="`/goods/${item.id}`">查看</RouterLink>
            <RouterLink v-if="item.status === 'ON_SALE' || item.status === 'OFF_SHELF'" class="secondary" :to="`/goods/${item.id}/edit`">编辑</RouterLink>
            <button v-if="item.status === 'ON_SALE'" class="danger" @click="offShelf(item.id)">下架</button>
            <button v-if="item.status === 'OFF_SHELF'" class="primary" @click="relist(item.id)">重新上架</button>
          </div>
        </div>
      </article>
    </div>
    <p v-if="goods.length === 0" class="muted">你还没有发布商品。</p>
  </section>
</template>
