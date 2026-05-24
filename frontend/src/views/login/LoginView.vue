<template>
  <div class="login-page">
    <div class="bg-mesh"></div>
    <div class="bg-orb bg-orb--1"></div>
    <div class="bg-orb bg-orb--2"></div>

    <div class="login-container">
      <div class="login-header">
        <div class="login-brand">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="var(--color-primary)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 7h-7m0 0H3m10 0v14m0-14l4-4m-4 4l-4-4"/>
          </svg>
        </div>
        <h1 class="login-title">闲鱼排单系统</h1>
        <p class="login-desc">管理您的排单，高效有序</p>
      </div>

      <div class="mode-tabs">
        <button class="mode-tab" :class="{ active: mode === 'xianyu' }" @click="mode = 'xianyu'">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          闲鱼登录
        </button>
        <button class="mode-tab" :class="{ active: mode === 'admin' }" @click="mode = 'admin'">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z"/></svg>
          管理员登录
        </button>
      </div>

      <div class="login-card">
        <transition name="card-swap" mode="out-in">
          <div v-if="mode === 'xianyu'" key="xianyu" class="login-form">
            <div class="form-group">
              <label class="form-label">闲鱼昵称</label>
              <input type="text" class="form-input" v-model="xianyuForm.xianyuId" placeholder="输入您的闲鱼昵称" />
            </div>
            <div class="form-group">
              <label class="form-label">卖家ID</label>
              <input type="text" class="form-input" v-model="xianyuForm.sellerId" placeholder="输入卖家ID（管理员的用户名）" />
            </div>
            <button class="btn-primary" :disabled="loading" @click="handleXianyuLogin">
              <span v-if="loading" class="spinner"></span>
              <span v-else>登录</span>
            </button>
            <label class="remember-row">
              <input type="checkbox" v-model="remember" />
              <span>记住本次输入</span>
            </label>
          </div>

          <div v-else key="admin" class="login-form">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input type="text" class="form-input" v-model="adminForm.username" placeholder="输入管理员用户名" />
            </div>
            <div class="form-group">
              <label class="form-label">密码</label>
              <div class="input-wrap">
                <input :type="showPwd ? 'text' : 'password'" class="form-input" v-model="adminForm.password" placeholder="输入密码" @keyup.enter="handleAdminLogin" />
                <button class="input-suffix" @click="showPwd = !showPwd">
                  <svg v-if="showPwd" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8Z"/><circle cx="12" cy="12" r="3"/></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                </button>
              </div>
            </div>
            <button class="btn-primary" :disabled="loading" @click="handleAdminLogin">
              <span v-if="loading" class="spinner"></span>
              <span v-else>登录</span>
            </button>
            <label class="remember-row">
              <input type="checkbox" v-model="remember" />
              <span>记住本次输入</span>
            </label>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { xianyuLogin, adminLogin } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const mode = ref<'xianyu' | 'admin'>('xianyu')
const loading = ref(false)
const showPwd = ref(false)
const remember = ref(true)
const xianyuForm = ref({ xianyuId: '', sellerId: '' })
const adminForm = ref({ username: '', password: '' })

onMounted(() => {
  const saved = localStorage.getItem('login_mode')
  if (saved === 'xianyu' || saved === 'admin') mode.value = saved
  remember.value = localStorage.getItem('login_remember') !== '0'
  if (remember.value) {
    const xid = localStorage.getItem('login_xianyuId')
    if (xid) xianyuForm.value.xianyuId = xid
    const sid = localStorage.getItem('login_sellerId')
    if (sid) xianyuForm.value.sellerId = sid
    const uname = localStorage.getItem('login_username')
    if (uname) adminForm.value.username = uname
    const pwd = localStorage.getItem('login_password')
    if (pwd) adminForm.value.password = pwd
  }
})

watch(mode, (v) => localStorage.setItem('login_mode', v))
watch(remember, (v) => {
  localStorage.setItem('login_remember', v ? '1' : '0')
  if (!v) {
    localStorage.removeItem('login_xianyuId')
    localStorage.removeItem('login_sellerId')
    localStorage.removeItem('login_username')
    localStorage.removeItem('login_password')
  }
})

async function handleXianyuLogin() {
  if (!xianyuForm.value.xianyuId || !xianyuForm.value.sellerId) {
    ElMessage.warning('请填写闲鱼昵称和卖家ID')
    return
  }
  loading.value = true
  try {
    const res = await xianyuLogin(xianyuForm.value)
    if (remember.value) {
      localStorage.setItem('login_xianyuId', xianyuForm.value.xianyuId)
      localStorage.setItem('login_sellerId', xianyuForm.value.sellerId)
    }
    auth.setAuth(res.token, res.role, res.nickname, res.tenantId)
    router.push('/user/home')
  } finally { loading.value = false }
}

async function handleAdminLogin() {
  if (!adminForm.value.username || !adminForm.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await adminLogin(adminForm.value)
    if (remember.value) {
      localStorage.setItem('login_username', adminForm.value.username)
      localStorage.setItem('login_password', adminForm.value.password)
    }
    auth.setAuth(res.token, res.role, res.nickname, res.tenantId)
    router.push(res.role === 'SUPER_ADMIN' ? '/super/manage' : '/admin/home')
  } finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
  background: linear-gradient(135deg, #EEF2FF 0%, #F8FAFC 40%, #F0FDF4 100%);
}
.bg-mesh {
  position: absolute; inset: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(37,99,235,0.06) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(5,150,105,0.06) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(124,58,237,0.04) 0%, transparent 60%);
}
.bg-orb { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.12; }
.bg-orb--1 { width: 400px; height: 400px; background: #2563EB; top: -100px; right: -100px; }
.bg-orb--2 { width: 300px; height: 300px; background: #059669; bottom: -80px; left: -80px; opacity: 0.08; }

.login-container { position: relative; z-index: 1; width: 420px; max-width: 90vw; }
.login-header { text-align: center; margin-bottom: var(--space-8); }
.login-brand { margin-bottom: var(--space-4); display: inline-flex; }
.login-title { font-size: 28px; font-weight: var(--font-weight-bold); color: var(--color-foreground); letter-spacing: -0.5px; margin-bottom: var(--space-2); }
.login-desc { color: var(--color-text-secondary); font-size: var(--font-size-sm); }

.mode-tabs {
  display: flex; gap: var(--space-2); margin-bottom: var(--space-4);
  background: var(--color-surface); border-radius: var(--radius-lg);
  padding: 4px; box-shadow: var(--shadow-xs);
}
.mode-tab {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--space-2);
  padding: 10px 16px; border-radius: var(--radius-md); border: none;
  background: transparent; color: var(--color-text-secondary);
  font-size: var(--font-size-sm); font-weight: var(--font-weight-medium);
  cursor: pointer; transition: all var(--transition-fast);
  font-family: var(--font-sans);
}
.mode-tab:hover { color: var(--color-text); }
.mode-tab.active { background: var(--color-primary); color: #FFF; box-shadow: var(--shadow-sm); }

.login-card {
  background: var(--color-surface); border-radius: var(--radius-xl);
  padding: var(--space-8); box-shadow: var(--shadow-lg); border: 1px solid var(--color-border-light);
}
.login-form { display: flex; flex-direction: column; gap: var(--space-5); }

.form-group { display: flex; flex-direction: column; gap: var(--space-2); }
.form-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); color: var(--color-text); margin-left: 2px; }
.form-input {
  width: 100%; padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius-md);
  background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-base);
  font-family: var(--font-sans); transition: all var(--transition-fast); outline: none;
}
.form-input::placeholder { color: var(--color-text-muted); }
.form-input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
.input-wrap { position: relative; }
.input-suffix { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; color: var(--color-text-muted); display: flex; padding: 4px; }

.btn-primary {
  width: 100%; padding: 12px 24px; background: var(--color-primary); color: #FFF;
  border: none; border-radius: var(--radius-md); font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold); cursor: pointer; transition: all var(--transition-fast);
  font-family: var(--font-sans); display: flex; align-items: center; justify-content: center; gap: var(--space-2);
  box-shadow: var(--shadow-sm);
}
.btn-primary:hover { background: var(--color-primary-dark); box-shadow: var(--shadow-md); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.spinner { width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #FFF; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.card-swap-enter-active, .card-swap-leave-active { transition: all 250ms cubic-bezier(0.16, 1, 0.3, 1); }
.card-swap-enter-from { opacity: 0; transform: translateY(8px); }
.card-swap-leave-to { opacity: 0; transform: translateY(-8px); }
@media (max-width: 640px) {
  .login-container { width: 100%; padding: var(--space-4); }
  .login-card { padding: var(--space-5); }
  .login-title { font-size: 22px; }
}
</style>
