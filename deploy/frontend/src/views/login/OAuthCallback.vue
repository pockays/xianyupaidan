<template>
  <div class="callback-container">
    <div class="callback-card">
      <div v-if="loading" class="callback-loading">
        <div class="spinner"></div>
        <p>正在登录中...</p>
      </div>

      <div v-else-if="loginType === 'user'" class="callback-form">
        <h3>闲鱼授权成功</h3>
        <p class="hint">请输入卖家ID，关联到对应的卖家</p>
        <input
          v-model="sellerId"
          class="form-input"
          placeholder="输入卖家ID"
          @keyup.enter="handleUserLogin"
        />
        <button class="btn-submit" :disabled="!sellerId.trim() || submitting" @click="handleUserLogin">
          <span v-if="submitting" class="spinner-small"></span>
          <span v-else>确认登录</span>
        </button>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      </div>

      <div v-else class="callback-loading">
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { oauthUserLogin, oauthAdminLogin } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const loading = ref(true)
const submitting = ref(false)
const loginType = ref<'user' | 'admin'>('user')
const sellerId = ref('')
const errorMsg = ref('')

onMounted(async () => {
  const code = route.query.code as string
  const state = route.query.state as string

  if (!code || !state) {
    errorMsg.value = '授权参数缺失，请返回重新登录'
    loading.value = false
    return
  }

  loginType.value = state.startsWith('admin') ? 'admin' : 'user'

  if (loginType.value === 'admin') {
    await doAdminLogin(code, state)
  } else {
    loading.value = false
  }
})

async function doAdminLogin(code: string, state: string) {
  try {
    const res = await oauthAdminLogin(code, state)
    auth.setAuth(res.token, res.role, res.nickname, res.tenantId)
    router.push('/admin/home')
  } catch (e: any) {
    errorMsg.value = e?.message || '管理员登录失败'
    loading.value = false
  }
}

async function handleUserLogin() {
  const code = route.query.code as string
  const state = route.query.state as string
  submitting.value = true
  errorMsg.value = ''
  try {
    const res = await oauthUserLogin(code, state, sellerId.value.trim())
    auth.setAuth(res.token, res.role, res.nickname, res.tenantId)
    router.push('/user/home')
  } catch (e: any) {
    errorMsg.value = e?.message || '登录失败，请检查卖家ID'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.callback-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
}
.callback-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: var(--space-10);
  box-shadow: var(--shadow-lg);
  max-width: 400px;
  width: 90vw;
  text-align: center;
}
.callback-loading { display: flex; flex-direction: column; align-items: center; gap: var(--space-4); }
.callback-loading p { color: var(--color-text-secondary); font-size: var(--font-size-sm); }
.spinner {
  width: 40px; height: 40px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
.spinner-small {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #FFF;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  display: inline-block;
}
@keyframes spin { to { transform: rotate(360deg); } }
.callback-form { display: flex; flex-direction: column; gap: var(--space-4); }
.callback-form h3 { font-size: var(--font-size-xl); color: var(--color-foreground); margin: 0; }
.hint { color: var(--color-text-secondary); font-size: var(--font-size-sm); margin: 0; }
.form-input {
  padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius-md);
  background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm);
  font-family: var(--font-sans); outline: none; text-align: center;
}
.form-input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
.btn-submit {
  padding: 10px 24px; border: none; border-radius: var(--radius-md);
  background: var(--color-primary); color: #FFF; font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium); cursor: pointer; font-family: var(--font-sans);
  display: flex; align-items: center; justify-content: center; gap: var(--space-2);
}
.btn-submit:hover { background: var(--color-primary-dark); }
.btn-submit:disabled { opacity: 0.5; cursor: not-allowed; }
.error-msg { color: var(--color-destructive); font-size: var(--font-size-sm); margin-top: var(--space-2); }
</style>
