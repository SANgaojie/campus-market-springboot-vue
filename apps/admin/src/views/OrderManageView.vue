<!--
  OrderManageView 模块

  @author 阿德
  @date 2026/05/19
-->
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { adminCancelOrder, adminRefundOrder, fetchAdminOrders } from '@/api/admin'
import type { OrderStatus, TradeOrder } from '@/api/types'
import { orderStatusLabel } from '@/utils/display'

const orders = ref<TradeOrder[]>([])
const status = ref<OrderStatus | ''>('')
const error = ref('')

const statuses: Array<OrderStatus | ''> = [
  '',
  'PENDING_PAYMENT',
  'PAID',
  'CANCELED',
  'COMPLETED',
  'REFUNDING',
  'REFUNDED',
]

async function load() {
  error.value = ''
  try {
    orders.value = await fetchAdminOrders(status.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载订单失败'
  }
}

async function forceCancel(orderId: number) {
  await adminCancelOrder(orderId)
  await load()
}

async function approveRefund(orderId: number) {
  await adminRefundOrder(orderId)
  await load()
}

onMounted(load)
</script>

<template>
  <section class="card">
    <div class="section-title">
      <div>
        <p class="eyebrow">Orders</p>
        <h2>订单管理</h2>
        <p class="muted">查看交易状态，必要时强制取消异常订单。</p>
      </div>
      <div class="actions">
        <select v-model="status" @change="load">
          <option v-for="item in statuses" :key="item || 'ALL'" :value="item">
            {{ item ? orderStatusLabel(item) : '全部状态' }}
          </option>
        </select>
        <button class="secondary" @click="load">刷新</button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-list">
      <div v-for="order in orders" :key="order.id" class="row">
        <div>
          <strong>{{ order.orderNo }}</strong>
          <p class="muted">商品: {{ order.goodsId }} ｜ 买家: {{ order.buyerId }} ｜ 卖家: {{ order.sellerId }} ｜ ¥{{ order.amount }}</p>
          <p class="muted">创建时间：{{ new Date(order.createdAt).toLocaleString() }}</p>
        </div>
        <div class="actions">
          <span class="badge">{{ orderStatusLabel(order.status) }}</span>
          <button
            v-if="order.status === 'PENDING_PAYMENT' || order.status === 'PAID'"
            class="danger"
            @click="forceCancel(order.id)"
          >
            强制取消
          </button>
          <button v-if="order.status === 'REFUNDING'" class="primary" @click="approveRefund(order.id)">
            确认退款
          </button>
        </div>
      </div>
    </div>
    <p v-if="orders.length === 0" class="muted">暂无订单。</p>
  </section>
</template>
