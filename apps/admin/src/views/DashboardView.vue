<!--
  DashboardView 模块

  @author 阿德
  @date 2026/05/16
-->
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchDashboardSummary } from '@/api/dashboard'
import type { AdminDashboardSummary } from '@/api/types'

const loading = ref(true)
const error = ref('')
const summary = ref<AdminDashboardSummary | null>(null)

const cards = computed(() => {
  if (!summary.value) return []
  const goodsOtherCount = Math.max(
    summary.value.goodsCount - summary.value.onSaleGoodsCount - summary.value.lockedGoodsCount - summary.value.soldGoodsCount,
    0,
  )
  const orderOtherCount = Math.max(
    summary.value.orderCount -
      summary.value.pendingPaymentOrderCount -
      summary.value.paidOrderCount -
      summary.value.completedOrderCount -
      summary.value.refundingOrderCount -
      summary.value.refundedOrderCount,
    0,
  )
  return [
    { label: '用户总数', value: summary.value.userCount, hint: `正常 ${summary.value.enabledUserCount} / 禁用 ${summary.value.disabledUserCount}` },
    { label: '分类数', value: summary.value.categoryCount, hint: `启用 ${summary.value.enabledCategoryCount} / 停用 ${summary.value.categoryCount - summary.value.enabledCategoryCount}` },
    { label: '商品总数', value: summary.value.goodsCount, hint: `在售 ${summary.value.onSaleGoodsCount} / 交易中 ${summary.value.lockedGoodsCount} / 已售 ${summary.value.soldGoodsCount} / 其他 ${goodsOtherCount}` },
    { label: '订单总数', value: summary.value.orderCount, hint: `待支付 ${summary.value.pendingPaymentOrderCount} / 已支付 ${summary.value.paidOrderCount} / 已完成 ${summary.value.completedOrderCount} / 退款中 ${summary.value.refundingOrderCount} / 已退款 ${summary.value.refundedOrderCount} / 其他 ${orderOtherCount}` },
    { label: '评论总数', value: summary.value.commentCount, hint: `可见 ${summary.value.visibleCommentCount} / 已删除 ${summary.value.deletedCommentCount}` },
  ]
})

onMounted(async () => {
  try {
    summary.value = await fetchDashboardSummary()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载概览失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section>
    <p v-if="loading" class="muted">加载中...</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div class="grid">
      <article v-for="card in cards" :key="card.label" class="card stat">
        <span class="muted">{{ card.label }}</span>
        <strong>{{ card.value }}</strong>
        <span class="muted">{{ card.hint }}</span>
      </article>
    </div>

    <article class="card panel" style="margin-top: 20px">
      <h2>待关注事项</h2>
      <div v-if="summary" class="table-list">
        <div class="row">
          <div>
            <strong>待支付订单</strong>
            <p class="muted">买家尚未完成支付的交易。</p>
          </div>
          <span class="badge">{{ summary.pendingPaymentOrderCount }}</span>
        </div>
        <div class="row">
          <div>
            <strong>已支付待完成订单</strong>
            <p class="muted">需要卖家确认完成的交易。</p>
          </div>
          <span class="badge">{{ summary.paidOrderCount }}</span>
        </div>
        <div class="row">
          <div>
            <strong>退款中订单</strong>
            <p class="muted">买家已申请退款，等待管理员确认。</p>
          </div>
          <span class="badge">{{ summary.refundingOrderCount }}</span>
        </div>
        <div class="row">
          <div>
            <strong>已删除评论</strong>
            <p class="muted">管理员或用户已软删除的评论。</p>
          </div>
          <span class="badge">{{ summary.deletedCommentCount }}</span>
        </div>
      </div>
    </article>
  </section>
</template>
