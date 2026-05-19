<!--
  FavoriteGoodsView 模块

  @author 阿德
  @date 2026/05/16
-->
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchFavoriteGoods, unfavoriteGoods } from '@/api/goods'
import type { Goods } from '@/api/types'
import { goodsPlaceholder } from '@/utils/display'

const goods = ref<Goods[]>([])
const error = ref('')

async function load() {
  error.value = ''
  try {
    goods.value = await fetchFavoriteGoods()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载收藏失败'
  }
}

async function remove(goodsId: number) {
  await unfavoriteGoods(goodsId)
  await load()
}

onMounted(load)
</script>

<template>
  <section>
    <h1 class="page-title">我的收藏</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="grid">
      <article v-for="item in goods" :key="item.id" class="card">
        <img class="thumb" :src="item.imageUrls[0] || goodsPlaceholder" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <p class="price">¥{{ item.price }}</p>
        <p class="muted">成色：{{ item.conditionLevel }}/5</p>
        <div class="actions">
          <RouterLink class="secondary" :to="`/goods/${item.id}`">查看详情</RouterLink>
          <button class="danger" @click="remove(item.id)">取消收藏</button>
        </div>
      </article>
    </div>
    <p v-if="goods.length === 0" class="muted">你还没有收藏商品。</p>
  </section>
</template>
