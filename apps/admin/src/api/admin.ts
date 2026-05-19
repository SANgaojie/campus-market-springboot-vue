import { http, request } from '@/api/http'
import type { Category, Goods, GoodsComment, OrderStatus, TradeOrder, UserProfile } from '@/api/types'

export function fetchAdminUsers() {
  return request<UserProfile[]>(http.get('/admin/users'))
}

export function enableUser(userId: number) {
  return request<UserProfile>(http.patch(`/admin/users/${userId}/enable`))
}

export function disableUser(userId: number) {
  return request<UserProfile>(http.patch(`/admin/users/${userId}/disable`))
}

export function fetchAdminGoods() {
  return request<Goods[]>(http.get('/admin/goods'))
}

export function adminOffShelfGoods(goodsId: number) {
  return request<Goods>(http.patch(`/admin/goods/${goodsId}/off-shelf`))
}

export function fetchAdminOrders(status?: OrderStatus | '') {
  return request<TradeOrder[]>(http.get('/admin/orders', { params: { status: status || undefined } }))
}

export function adminCancelOrder(orderId: number) {
  return request<TradeOrder>(http.patch(`/admin/orders/${orderId}/cancel`))
}

export function adminRefundOrder(orderId: number) {
  return request<TradeOrder>(http.patch(`/admin/orders/${orderId}/refund`))
}

export function fetchAdminCategories() {
  return request<Category[]>(http.get('/admin/categories'))
}

export function createAdminCategory(payload: { name: string; sortOrder: number }) {
  return request<Category>(http.post('/admin/categories', payload))
}

export function updateAdminCategory(categoryId: number, payload: { name: string; sortOrder: number }) {
  return request<Category>(http.patch(`/admin/categories/${categoryId}`, payload))
}

export function enableAdminCategory(categoryId: number) {
  return request<Category>(http.patch(`/admin/categories/${categoryId}/enable`))
}

export function disableAdminCategory(categoryId: number) {
  return request<Category>(http.patch(`/admin/categories/${categoryId}/disable`))
}

export function fetchAdminComments() {
  return request<GoodsComment[]>(http.get('/admin/comments'))
}

export function adminDeleteComment(commentId: number) {
  return request<void>(http.delete(`/admin/comments/${commentId}`))
}
