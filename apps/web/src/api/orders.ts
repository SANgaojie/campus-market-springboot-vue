import { http, request } from '@/api/http'
import type { TradeOrder } from '@/api/types'

export function createOrder(goodsId: number) {
  return request<TradeOrder>(http.post('/orders', { goodsId }))
}

export function fetchBoughtOrders() {
  return request<TradeOrder[]>(http.get('/orders/bought'))
}

export function fetchSoldOrders() {
  return request<TradeOrder[]>(http.get('/orders/sold'))
}

export function cancelOrder(orderId: number) {
  return request<TradeOrder>(http.patch(`/orders/${orderId}/cancel`))
}

export function payOrder(orderId: number) {
  return request<TradeOrder>(http.patch(`/orders/${orderId}/pay`))
}

export function requestRefund(orderId: number) {
  return request<TradeOrder>(http.patch(`/orders/${orderId}/refund`))
}

export function completeOrder(orderId: number) {
  return request<TradeOrder>(http.patch(`/orders/${orderId}/complete`))
}
