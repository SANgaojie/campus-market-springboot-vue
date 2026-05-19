/**
 * 会话状态管理
 *
 * @author 阿德
 * @date 2026/05/15
 */
import { reactive } from 'vue'
import type { UserProfile } from '@/api/types'

const TOKEN_KEY = 'campus_market_admin_token'
const USER_KEY = 'campus_market_admin_user'

function readUser(): UserProfile | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserProfile
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const adminSession = reactive({
  token: localStorage.getItem(TOKEN_KEY) ?? '',
  user: readUser(),
  get isLoggedIn() {
    return Boolean(this.token)
  },
  get isAdmin() {
    return this.user?.roles.includes('ROLE_ADMIN') ?? false
  },
  setSession(token: string, user: UserProfile) {
    this.token = token
    this.user = user
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  },
  logout() {
    this.token = ''
    this.user = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },
})
