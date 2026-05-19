import type { GoodsStatus, OrderStatus } from '@/api/types'

export const goodsPlaceholder =
  'data:image/svg+xml;utf8,' +
  encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 480">
  <rect width="640" height="480" rx="32" fill="#eef3ff"/>
  <rect x="128" y="132" width="384" height="216" rx="24" fill="#dbe7ff"/>
  <circle cx="238" cy="218" r="34" fill="#a8bdf7"/>
  <path d="M168 318l112-96 78 66 48-42 66 72H168z" fill="#7d98e8"/>
  <text x="320" y="398" text-anchor="middle" font-family="Arial, sans-serif" font-size="30" font-weight="700" fill="#5d6f99">暂无图片</text>
</svg>`)

const goodsStatusLabels: Record<GoodsStatus, string> = {
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

export function goodsStatusLabel(status: GoodsStatus) {
  return goodsStatusLabels[status] ?? status
}

export function orderStatusLabel(status: OrderStatus) {
  return orderStatusLabels[status] ?? status
}
