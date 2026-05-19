<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createGoodsComment, deleteGoodsComment, fetchGoodsComments } from '@/api/comments'
import { favoriteGoods, fetchGoodsDetail, fetchFavoriteGoods, unfavoriteGoods } from '@/api/goods'
import { createOrder } from '@/api/orders'
import type { Goods, GoodsComment } from '@/api/types'
import { useAuthStore } from '@/stores/auth'
import { goodsPlaceholder, goodsStatusLabel } from '@/utils/display'

const props = defineProps<{ id: string }>()
const router = useRouter()
const auth = useAuthStore()
const goods = ref<Goods | null>(null)
const comments = ref<GoodsComment[]>([])
const commentContent = ref('')
const isFavorite = ref(false)
const error = ref('')
const commentError = ref('')
const message = ref('')

onMounted(async () => {
  try {
    goods.value = await fetchGoodsDetail(Number(props.id))
    await Promise.all([loadFavoriteState(), loadComments()])
  } catch (err) {
    error.value = err instanceof Error ? err.message : '商品不存在或已下架'
  }
})

async function loadFavoriteState() {
  if (!auth.isLoggedIn) return
  const favorites = await fetchFavoriteGoods()
  isFavorite.value = favorites.some((item) => item.id === Number(props.id))
}

async function loadComments() {
  comments.value = await fetchGoodsComments(Number(props.id))
}

async function buy() {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: `/goods/${props.id}` } })
    return
  }
  error.value = ''
  message.value = ''
  try {
    const order = await createOrder(Number(props.id))
    message.value = `下单成功，订单号：${order.orderNo}`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '下单失败'
  }
}

async function toggleFavorite() {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: `/goods/${props.id}` } })
    return
  }
  error.value = ''
  message.value = ''
  try {
    if (isFavorite.value) {
      await unfavoriteGoods(Number(props.id))
      isFavorite.value = false
      message.value = '已取消收藏'
    } else {
      await favoriteGoods(Number(props.id))
      isFavorite.value = true
      message.value = '已收藏'
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '操作失败'
  }
}

async function submitComment() {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: `/goods/${props.id}` } })
    return
  }
  commentError.value = ''
  try {
    await createGoodsComment(Number(props.id), commentContent.value)
    commentContent.value = ''
    await loadComments()
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : '评论失败'
  }
}

function canDeleteComment(comment: GoodsComment) {
  return auth.user?.id === comment.userId || auth.user?.id === goods.value?.sellerId
}

async function removeComment(commentId: number) {
  commentError.value = ''
  try {
    await deleteGoodsComment(commentId)
    await loadComments()
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : '删除评论失败'
  }
}
</script>

<template>
  <section v-if="goods" class="detail-page">
    <div class="card">
      <div style="display: grid; grid-template-columns: minmax(0, 1fr) minmax(320px, 1fr); gap: 28px">
        <div>
          <img class="thumb" :src="goods.imageUrls[0] || goodsPlaceholder" :alt="goods.title" />
          <div class="grid" style="grid-template-columns: repeat(auto-fill, minmax(90px, 1fr)); margin-top: 12px">
            <img v-for="url in goods.imageUrls.slice(1)" :key="url" class="thumb" :src="url" :alt="goods.title" />
          </div>
        </div>
        <div>
          <span class="badge">{{ goodsStatusLabel(goods.status) }}</span>
          <h1 class="page-title" style="margin-top: 12px">{{ goods.title }}</h1>
          <p class="price">¥{{ goods.price }}</p>
          <p>成色：{{ goods.conditionLevel }}/5</p>
          <p class="muted">卖家 #{{ goods.sellerId }} · 分类 #{{ goods.categoryId }} · 发布于 {{ new Date(goods.createdAt).toLocaleString() }}</p>
          <p class="muted">{{ goods.description || '卖家暂未填写描述' }}</p>
          <div class="actions">
            <button class="primary" @click="buy">立即下单</button>
            <button class="secondary" @click="toggleFavorite">{{ isFavorite ? '取消收藏' : '收藏商品' }}</button>
            <RouterLink class="secondary" to="/">返回列表</RouterLink>
          </div>
          <p v-if="message" class="success">{{ message }}</p>
          <p v-if="error" class="error">{{ error }}</p>
        </div>
      </div>
    </div>

    <section class="card comments-card">
      <h2>商品评论</h2>
      <form class="comment-form" @submit.prevent="submitComment">
        <textarea v-model="commentContent" maxlength="500" placeholder="有问题可以问问卖家，比如：还在吗？能便宜吗？" />
        <div class="actions">
          <button class="primary" type="submit">发表评论</button>
          <span class="muted">{{ commentContent.length }}/500</span>
        </div>
      </form>
      <p v-if="commentError" class="error">{{ commentError }}</p>

      <div class="comment-list">
        <article v-for="comment in comments" :key="comment.id" class="comment-item">
          <div>
            <p>{{ comment.content }}</p>
            <p class="muted">用户 #{{ comment.userId }} · {{ new Date(comment.createdAt).toLocaleString() }}</p>
          </div>
          <button v-if="canDeleteComment(comment)" class="danger" @click="removeComment(comment.id)">删除</button>
        </article>
      </div>
      <p v-if="comments.length === 0" class="muted">还没有评论，来问第一个问题吧。</p>
    </section>
  </section>

  <p v-else-if="error" class="error">{{ error }}</p>
  <p v-else class="muted">加载中...</p>
</template>
