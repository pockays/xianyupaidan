import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const tenantId = ref(localStorage.getItem('tenantId') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isUser = computed(() => role.value === 'USER')
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isSuperAdmin = computed(() => role.value === 'SUPER_ADMIN')

  function setAuth(t: string, r: string, n: string, tid?: string) {
    token.value = t
    role.value = r
    nickname.value = n
    tenantId.value = tid || ''
    localStorage.setItem('token', t)
    localStorage.setItem('role', r)
    localStorage.setItem('nickname', n)
    if (tid) localStorage.setItem('tenantId', tid)
  }

  function logout() {
    token.value = ''
    role.value = ''
    nickname.value = ''
    tenantId.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('nickname')
    localStorage.removeItem('tenantId')
  }

  return { token, role, nickname, isLoggedIn, isUser, isAdmin, isSuperAdmin, setAuth, logout }
})
