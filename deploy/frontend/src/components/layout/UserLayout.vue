<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar-brand">
        <div class="brand-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 7h-7m0 0H3m10 0v14m0-14l4-4m-4 4l-4-4"/></svg>
        </div>
        <span class="brand-text">闲鱼排单</span>
      </div>
      <div class="topbar-actions">
        <span class="user-chip">
          <span class="user-avatar">{{ auth.nickname?.[0] || 'U' }}</span>
          <span class="user-name">{{ auth.nickname }}</span>
        </span>
        <button class="btn-logout" @click="handleLogout" title="退出登录">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        </button>
      </div>
    </header>
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-shell { min-height: 100vh; background: var(--color-bg); }
.topbar {
  height: 56px; background: var(--color-surface);
  border-bottom: 1px solid var(--color-border-light);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 var(--space-6); position: sticky; top: 0; z-index: 100;
  backdrop-filter: blur(12px); background: rgba(255,255,255,0.85);
}
.topbar-brand { display: flex; align-items: center; gap: var(--space-3); }
.brand-icon { color: var(--color-primary); display: flex; align-items: center; }
.brand-text { font-size: var(--font-size-lg); font-weight: var(--font-weight-semibold); color: var(--color-foreground); letter-spacing: -0.3px; }
.topbar-actions { display: flex; align-items: center; gap: var(--space-4); }
.user-chip { display: flex; align-items: center; gap: var(--space-2); }
.user-avatar {
  width: 30px; height: 30px; border-radius: var(--radius-full);
  background: var(--color-primary-bg); color: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold);
}
.user-name { color: var(--color-text-secondary); font-size: var(--font-size-sm); }
.btn-logout {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: var(--radius-md);
  border: 1px solid var(--color-border); background: var(--color-surface);
  color: var(--color-text-secondary); cursor: pointer;
  transition: all var(--transition-fast);
}
.btn-logout:hover { background: var(--color-destructive-bg); color: var(--color-destructive); border-color: var(--color-destructive); }
.main-content { max-width: 960px; margin: 0 auto; padding: var(--space-6) var(--space-4); }
@media (max-width: 640px) {
  .main-content { padding: var(--space-3) var(--space-3); }
  .brand-text { display: none; }
  .topbar { padding: 0 var(--space-4); }
  .user-name { display: none; }
}
</style>
