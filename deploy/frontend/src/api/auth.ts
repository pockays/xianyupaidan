import request from './request'

export interface LoginResponse {
  token: string
  role: string
  nickname: string
  tenantId: string
}

export function xianyuLogin(data: { xianyuId: string; sellerId: string }) {
  return request.post<any, LoginResponse>('/auth/xianyu-login', data)
}

export function adminLogin(data: { username: string; password: string }) {
  return request.post<any, LoginResponse>('/auth/admin-login', data)
}

export function getOAuthUrl(state: string) {
  return request.post<any, { url: string }>('/auth/oauth/authorize', { state })
}

export function oauthUserLogin(code: string, state: string, sellerId: string) {
  return request.post<any, LoginResponse>('/auth/oauth/user-login', { code, state, sellerId })
}

export function oauthAdminLogin(code: string, state: string) {
  return request.post<any, LoginResponse>('/auth/oauth/admin-login', { code, state })
}

export function getMe() {
  return request.get('/auth/me')
}
