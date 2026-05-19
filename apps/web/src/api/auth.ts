import { http, request } from '@/api/http'
import type { AuthResponse, UserProfile } from '@/api/types'

export interface RegisterPayload {
  username: string
  password: string
  nickname: string
}

export interface LoginPayload {
  username: string
  password: string
}

export function register(payload: RegisterPayload) {
  return request<AuthResponse>(http.post('/auth/register', payload))
}

export function login(payload: LoginPayload) {
  return request<AuthResponse>(http.post('/auth/login', payload))
}

export function fetchMe() {
  return request<UserProfile>(http.get('/auth/me'))
}

export function updateProfile(payload: { nickname: string }) {
  return request<UserProfile>(http.patch('/auth/me', payload))
}

export function changePassword(payload: { oldPassword: string; newPassword: string }) {
  return request<void>(http.patch('/auth/password', payload))
}
