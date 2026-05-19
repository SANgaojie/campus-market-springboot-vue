import { http, request } from '@/api/http'
import type { Category, Goods } from '@/api/types'

export interface GoodsPayload {
  categoryId: number
  title: string
  description: string
  price: number
  conditionLevel: number
  imageUrls: string[]
}

export function fetchCategories() {
  return request<Category[]>(http.get('/categories'))
}

export function fetchGoods(categoryId?: number) {
  return request<Goods[]>(http.get('/goods', { params: { categoryId } }))
}

export function fetchGoodsDetail(goodsId: number) {
  return request<Goods>(http.get(`/goods/${goodsId}`))
}

export function createGoods(payload: GoodsPayload) {
  return request<Goods>(http.post('/goods', payload))
}

export function fetchMyGoods() {
  return request<Goods[]>(http.get('/goods/mine'))
}

export function fetchMyGoodsDetail(goodsId: number) {
  return request<Goods>(http.get(`/goods/mine/${goodsId}`))
}

export function fetchFavoriteGoods() {
  return request<Goods[]>(http.get('/goods/favorites'))
}

export function favoriteGoods(goodsId: number) {
  return request<void>(http.post(`/goods/${goodsId}/favorite`))
}

export function unfavoriteGoods(goodsId: number) {
  return request<void>(http.delete(`/goods/${goodsId}/favorite`))
}

export function updateGoods(goodsId: number, payload: GoodsPayload) {
  return request<Goods>(http.put(`/goods/${goodsId}`, payload))
}

export function offShelfGoods(goodsId: number) {
  return request<Goods>(http.patch(`/goods/${goodsId}/off-shelf`))
}

export function relistGoods(goodsId: number) {
  return request<Goods>(http.patch(`/goods/${goodsId}/relist`))
}
