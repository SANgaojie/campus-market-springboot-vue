/**
 * comments 模块
 *
 * @author 阿德
 * @date 2026/05/16
 */
import { http, request } from '@/api/http'
import type { GoodsComment } from '@/api/types'

export function fetchGoodsComments(goodsId: number) {
  return request<GoodsComment[]>(http.get(`/goods/${goodsId}/comments`))
}

export function createGoodsComment(goodsId: number, content: string) {
  return request<GoodsComment>(http.post(`/goods/${goodsId}/comments`, { content }))
}

export function deleteGoodsComment(commentId: number) {
  return request<void>(http.delete(`/goods/comments/${commentId}`))
}
