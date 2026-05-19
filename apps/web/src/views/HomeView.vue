<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchCategories, fetchGoods } from '@/api/goods'
import type { Category, Goods } from '@/api/types'
import { goodsPlaceholder } from '@/utils/display'

const categories = ref<Category[]>([])
const goods = ref<Goods[]>([])
const categoryId = ref<number | ''>('')
const loading = ref(false)
const error = ref('')
const pageSize = 24
const visibleCount = ref(pageSize)

const displayedGoods = computed(() => goods.value.slice(0, visibleCount.value))
const remainingCount = computed(() => Math.max(goods.value.length - displayedGoods.value.length, 0))

async function loadGoods() {
  loading.value = true
  error.value = ''
  try {
    visibleCount.value = pageSize
    goods.value = await fetchGoods(categoryId.value === '' ? undefined : categoryId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载商品失败'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    categories.value = await fetchCategories()
    await loadGoods()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载数据失败'
  }
})
</script>

<template>
  <section>
    <h1 class="page-title">发现校园好物</h1>
    <p class="muted">先做最小可用版：商品列表、详情、下单链路已经和后端打通。</p>

    <div class="card" style="margin: 24px 0">
      <label>
        商品分类
        <select v-model="categoryId" @change="loadGoods">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </label>
    </div>

    <p v-if="loading" class="muted">加载中...</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div class="list-summary" v-if="!loading && goods.length > 0">
      <span class="muted">共 {{ goods.length }} 件在售，当前展示 {{ displayedGoods.length }} 件</span>
    </div>

    <div class="grid">
      <RouterLink v-for="item in displayedGoods" :key="item.id" class="card" :to="`/goods/${item.id}`" style="text-decoration: none; color: inherit">
        <img class="thumb" :src="item.imageUrls[0] || goodsPlaceholder" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <p class="muted">成色：{{ item.conditionLevel }}/5</p>
        <p class="price">¥{{ item.price }}</p>
      </RouterLink>
    </div>

    <div v-if="remainingCount > 0" class="load-more-row">
      <button class="secondary" type="button" @click="visibleCount += pageSize">
        再加载 {{ Math.min(pageSize, remainingCount) }} 件
      </button>
    </div>

    <p v-if="!loading && goods.length === 0" class="muted">暂时没有在售商品。</p>
  </section>
</template>
