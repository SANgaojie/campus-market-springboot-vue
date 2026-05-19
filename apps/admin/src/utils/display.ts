import type { Goods, OrderStatus } from '@/api/types'

const goodsStatusLabels: Record<Goods['status'], string> = {
  DRAFT: '草稿',
  ON_SALE: '在售',
  LOCKED: '交易中',
  SOLD: '已售出',
  OFF_SHELF: '已下架',
}

const orderStatusLabels: Record<OrderStatus, string> = {
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  CANCELED: '已取消',
  COMPLETED: '已完成',
  REFUNDING: '退款中',
  REFUNDED: '已退款',
}

export function goodsStatusLabel(status: Goods['status']) {
  return goodsStatusLabels[status] ?? status
}

export function orderStatusLabel(status: OrderStatus) {
  return orderStatusLabels[status] ?? status
}
