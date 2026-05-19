import { http, request } from '@/api/http'

export interface UploadResponse {
  url: string
}

export function uploadGoodsImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<UploadResponse>(
    http.post('/images/goods', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  )
}
