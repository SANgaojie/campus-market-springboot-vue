export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface UserProfile {
  id: number
  username: string
  nickname: string
  status: number
  roles: string[]
}

export interface AuthResponse {
  token: string
  user: UserProfile
}

export interface Category {
  id: number
  name: string
  sortOrder: number
  enabled: number
}

export interface Goods {
  id: number
  sellerId: number
  categoryId: number
  title: string
  description: string | null
  price: number
  conditionLevel: number
  status: 'DRAFT' | 'ON_SALE' | 'LOCKED' | 'SOLD' | 'OFF_SHELF'
  createdAt: string
  imageUrls: string[]
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

export interface GoodsComment {
  id: number
  goodsId: number
  userId: number
  content: string
  deleted: number
  createdAt: string
}

export interface AdminDashboardSummary {
  userCount: number
  enabledUserCount: number
  disabledUserCount: number
  categoryCount: number
  enabledCategoryCount: number
  goodsCount: number
  onSaleGoodsCount: number
  lockedGoodsCount: number
  soldGoodsCount: number
  orderCount: number
  pendingPaymentOrderCount: number
  paidOrderCount: number
  completedOrderCount: number
  refundingOrderCount: number
  refundedOrderCount: number
  commentCount: number
  visibleCommentCount: number
  deletedCommentCount: number
}
