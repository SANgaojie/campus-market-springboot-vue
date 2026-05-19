export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface AuthResponse {
  token: string
  user: UserProfile
}

export interface UserProfile {
  id: number
  username: string
  nickname: string
  status: number
  roles: string[]
}

export interface Category {
  id: number
  name: string
}

export type GoodsStatus = 'DRAFT' | 'ON_SALE' | 'LOCKED' | 'SOLD' | 'OFF_SHELF'

export interface Goods {
  id: number
  sellerId: number
  categoryId: number
  title: string
  description: string | null
  price: number
  conditionLevel: number
  status: GoodsStatus
  createdAt: string
  imageUrls: string[]
}

export interface GoodsComment {
  id: number
  goodsId: number
  userId: number
  content: string
  createdAt: string
}

export type OrderStatus = 'PENDING_PAYMENT' | 'PAID' | 'CANCELED' | 'COMPLETED' | 'REFUNDING' | 'REFUNDED'

export interface TradeOrder {
  id: number
  orderNo: string
  goodsId: number
  buyerId: number
  sellerId: number
  amount: number
  status: OrderStatus
  createdAt: string
}
