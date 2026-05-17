import request from './request'
import type { OrderItem, OrderDetail, CreateOrderData } from './user'

export interface SystemConfig {
  id: number
  orderEnabled: number
  announcement: string
}

export interface PresetTag {
  id: number
  name: string
  sortOrder: number
}

export function getAdminOrders(params: {
  status?: string
  keyword?: string
  startDate?: string
  endDate?: string
  asc?: boolean
}) {
  return request.get<any, OrderItem[]>('/admin/orders', { params })
}

export function getAdminOrderDetail(id: number) {
  return request.get<any, OrderDetail>(`/admin/orders/${id}`)
}

export function updateOrderStatus(id: number, status: string) {
  return request.put(`/admin/orders/${id}/status`, { status })
}

export function updateOrderItem(orderId: number, itemId: number, data: any) {
  return request.put(`/admin/orders/${orderId}/items/${itemId}`, data)
}

export function addOrderItem(orderId: number, data: { categoryId: number; linkUrl?: string; note?: string }) {
  return request.post(`/admin/orders/${orderId}/items`, data)
}

export function deleteOrderItem(orderId: number, itemId: number) {
  return request.delete(`/admin/orders/${orderId}/items/${itemId}`)
}

export function adminCreateOrder(data: CreateOrderData) {
  return request.post<any, number>('/admin/orders', data)
}

export function addOrderCategories(orderId: number, data: CreateOrderData) {
  return request.post(`/admin/orders/${orderId}/categories`, data)
}

export function adminDeleteOrder(id: number) {
  return request.delete(`/admin/orders/${id}`)
}

export function getTags() {
  return request.get<any, PresetTag[]>('/admin/tags')
}

export function createTag(data: { name: string; sortOrder?: number }) {
  return request.post<any, PresetTag>('/admin/tags', data)
}

export function updateTag(id: number, data: { name: string; sortOrder?: number }) {
  return request.put(`/admin/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return request.delete(`/admin/tags/${id}`)
}

export function getConfig() {
  return request.get<any, SystemConfig>('/admin/config')
}

export function updateConfig(data: { orderEnabled?: number; announcement?: string }) {
  return request.put('/admin/config', data)
}
