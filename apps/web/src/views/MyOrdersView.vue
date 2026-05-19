<!--
  MyOrdersView 模块

  @author 阿德
  @date 2026/05/07
-->
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { cancelOrder, completeOrder, fetchBoughtOrders, fetchSoldOrders, payOrder, requestRefund } from '@/api/orders'
import type { TradeOrder } from '@/api/types'
import { orderStatusLabel } from '@/utils/display'

const bought = ref<TradeOrder[]>([])
const sold = ref<TradeOrder[]>([])
const error = ref('')

async function load() {
  error.value = ''
  try {
    const [boughtData, soldData] = await Promise.all([fetchBoughtOrders(), fetchSoldOrders()])
    bought.value = boughtData
    sold.value = soldData
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载订单失败'
  }
}

async function cancel(id: number) {
  await cancelOrder(id)
  await load()
}

async function pay(id: number) {
  await payOrder(id)
  await load()
}

async function refund(id: number) {
  await requestRefund(id)
  await load()
}

async function complete(id: number) {
  await completeOrder(id)
  await load()
}

onMounted(load)
</script>

<template>
  <section>
    <h1 class="page-title">我的订单</h1>
    <p v-if="error" class="error">{{ error }}</p>

    <h2>我买到的</h2>
    <div class="table-list">
      <article v-for="order in bought" :key="order.id" class="card">
        <span class="badge">{{ orderStatusLabel(order.status) }}</span>
        <h3>{{ order.orderNo }}</h3>
        <p>商品 ID：{{ order.goodsId }} ｜ 金额：¥{{ order.amount }}</p>
        <div class="actions">
          <button v-if="order.status === 'PENDING_PAYMENT'" class="danger" @click="cancel(order.id)">取消订单</button>
          <button v-if="order.status === 'PENDING_PAYMENT'" class="primary" @click="pay(order.id)">确认支付</button>
          <button v-if="order.status === 'PAID'" class="danger" @click="refund(order.id)">申请退款</button>
          <span v-if="order.status === 'PAID'" class="muted">已支付，等待卖家确认完成</span>
          <span v-if="order.status === 'REFUNDING'" class="muted">退款处理中，等待管理员确认</span>
          <span v-if="order.status === 'REFUNDED'" class="muted">已退款</span>
        </div>
      </article>
      <p v-if="bought.length === 0" class="muted">暂无买到的订单。</p>
    </div>

    <h2 style="margin-top: 32px">我卖出的</h2>
    <div class="table-list">
      <article v-for="order in sold" :key="order.id" class="card">
        <span class="badge">{{ orderStatusLabel(order.status) }}</span>
        <h3>{{ order.orderNo }}</h3>
        <p>商品 ID：{{ order.goodsId }} ｜ 金额：¥{{ order.amount }}</p>
        <div class="actions">
          <button v-if="order.status === 'PENDING_PAYMENT'" class="danger" @click="cancel(order.id)">取消交易</button>
          <span v-if="order.status === 'PENDING_PAYMENT'" class="muted">等待买家确认支付</span>
          <button v-if="order.status === 'PAID'" class="primary" @click="complete(order.id)">确认完成</button>
          <span v-if="order.status === 'REFUNDING'" class="muted">买家已申请退款，等待管理员处理</span>
          <span v-if="order.status === 'REFUNDED'" class="muted">订单已退款</span>
        </div>
      </article>
      <p v-if="sold.length === 0" class="muted">暂无卖出的订单。</p>
    </div>
  </section>
</template>
