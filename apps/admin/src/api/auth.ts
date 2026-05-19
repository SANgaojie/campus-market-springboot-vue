/**
 * 认证接口封装
 *
 * @author 阿德
 * @date 2026/05/08
 */
import { http, request } from '@/api/http'
import type { AuthResponse } from '@/api/types'

export function login(username: string, password: string) {
  return request<AuthResponse>(http.post('/auth/login', { username, password }))
}
