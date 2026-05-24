import request from './request'

export interface AdminInfo {
  id: number
  tenantId: string
  username: string
  xianyuId: string
  email: string
  status: number
  createdAt: string
}

export function getAdmins() {
  return request.get<any, AdminInfo[]>('/super/admins')
}

export function createAdmin(data: { username: string; password: string; email?: string; xianyuId?: string }) {
  return request.post<any, AdminInfo>('/super/admins', data)
}

export function updateAdmin(id: number, data: { username?: string; password?: string; email?: string; xianyuId?: string }) {
  return request.put(`/super/admins/${id}`, data)
}

export function deleteAdmin(id: number) {
  return request.delete(`/super/admins/${id}`)
}

export function toggleAdminStatus(id: number) {
  return request.put(`/super/admins/${id}/status`)
}
