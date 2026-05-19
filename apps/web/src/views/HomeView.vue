<!--
  HomeView 模块

  @author 阿德
  @date 2026/05/09
-->
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchCategories, fetchGoods } from '@/api/goods'
import type { Category, Goods } from '@/api/types'
import { goodsPlaceholder } from '@/utils/display'

const categories = ref<Category[]>([])
const goods = ref<Goods[]>([])
const categoryId = ref<number | ''>('')
const keyword = ref('')
const minPrice = ref<number | ''>('')
const maxPrice = ref<number | ''>('')
const minCondition = ref<number | ''>('')
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
    goods.value = await fetchGoods({
      categoryId: categoryId.value === '' ? undefined : categoryId.value,
      keyword: keyword.value.trim() || undefined,
      minPrice: minPrice.value === '' ? undefined : minPrice.value,
      maxPrice: maxPrice.value === '' ? undefined : maxPrice.value,
      minCondition: minCondition.value === '' ? undefined : minCondition.value,
    })
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
    <p class="muted">按关键词、分类、价格和成色快速筛选，找到合适的校园闲置。</p>

    <form class="card search-panel" @submit.prevent="loadGoods">
      <label>
        关键词
        <input v-model="keyword" placeholder="搜索商品标题或描述" />
      </label>
      <label>
        商品分类
        <select v-model.number="categoryId">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </label>
      <label>
        最低价格
        <input v-model.number="minPrice" type="number" min="0" placeholder="不限" />
      </label>
      <label>
        最高价格
        <input v-model.number="maxPrice" type="number" min="0" placeholder="不限" />
      </label>
      <label>
        最低成色
        <select v-model.number="minCondition">
          <option value="">不限</option>
          <option v-for="level in [5, 4, 3, 2, 1]" :key="level" :value="level">{{ level }} 成新以上</option>
        </select>
      </label>
      <div class="search-actions">
        <button class="primary" type="submit">搜索商品</button>
        <button class="secondary" type="button" @click="keyword = ''; categoryId = ''; minPrice = ''; maxPrice = ''; minCondition = ''; loadGoods()">重置</button>
      </div>
    </form>

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
