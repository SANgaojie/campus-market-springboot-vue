import { http, request } from '@/api/http'
import type { AdminDashboardSummary } from '@/api/types'

export function fetchDashboardSummary() {
  return request<AdminDashboardSummary>(http.get('/admin/dashboard'))
}
