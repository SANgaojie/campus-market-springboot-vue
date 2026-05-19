/**
 * HTTP 请求封装
 *
 * @author 阿德
 * @date 2026/05/06
 */
import axios from 'axios'
import { adminSession } from '@/api/session'
import type { ApiResponse } from '@/api/types'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  if (adminSession.token) {
    config.headers.Authorization = `Bearer ${adminSession.token}`
  }
  return config
})

export async function request<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise
  return response.data.data
}
