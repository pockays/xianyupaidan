import request from './request'

export interface UserHomeData {
  orderEnabled: boolean
  announcement: string
  totalOrders: number
  waitingOrders: number
  currentOrders: number
  recentOrders: OrderItem[]
}

export interface OrderItem {
  id: number
  userId: number
  nickname: string
  status: string
  totalPrice: number
  createdAt: string
}

export interface OrderDetail {
  id: number
  userId: number
  nickname: string
  email: string
  status: string
  totalPrice: number
  submitted: number
  createdAt: string
  updatedAt: string
  categories: CategoryDetail[]
}

export interface CategoryDetail {
  id: number
  categoryName: string
  sortOrder: number
  items: ItemDetail[]
}

export interface ItemDetail {
  id: number
  linkUrl: string
  note: string
  price: number
  status: string
  sortOrder: number
}

export interface CreateOrderData {
  email?: string
  categories: {
    categoryName: string
    items: {
      linkUrl: string
      note: string
    }[]
  }[]
}

export function getUserHome() {
  return request.get<any, UserHomeData>('/user/home')
}

export function createOrder(data: CreateOrderData) {
  return request.post<any, number>('/user/orders', data)
}

export function getUserOrders() {
  return request.get<any, OrderItem[]>('/user/orders')
}

export function getUserOrderDetail(id: number) {
  return request.get<any, OrderDetail>(`/user/orders/${id}`)
}

export function updateUserOrder(id: number, data: CreateOrderData) {
  return request.put(`/user/orders/${id}`, data)
}

export function submitOrder(id: number) {
  return request.post(`/user/orders/${id}/submit`)
}

export function deleteUserOrder(id: number) {
  return request.delete(`/user/orders/${id}`)
}
