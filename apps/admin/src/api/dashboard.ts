/**
 * 后台概览接口封装
 *
 * @author 阿德
 * @date 2026/05/08
 */
import { http, request } from '@/api/http'
import type { AdminDashboardSummary } from '@/api/types'

export function fetchDashboardSummary() {
  return request<AdminDashboardSummary>(http.get('/admin/dashboard'))
}
