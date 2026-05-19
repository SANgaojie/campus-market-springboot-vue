/**
 * HTTP 请求封装
 *
 * @author 阿德
 * @date 2026/05/08
 */
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import type { ApiResponse } from '@/api/types'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

export async function request<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise
  return response.data.data
}
