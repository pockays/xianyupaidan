<template>
  <div class="super-shell">
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z"/></svg>
        </div>
        <span class="brand-text">超级管理员</span>
      </div>
      <div class="topbar-right">
        <span class="user-chip">
          <span class="user-avatar">{{ auth.nickname?.[0] || 'S' }}</span>
          <span class="user-name">{{ auth.nickname }}</span>
        </span>
        <button class="btn-logout" @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
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
function handleLogout() { auth.logout(); router.push('/login') }
</script>

<style scoped>
.super-shell { min-height: 100vh; background: var(--color-bg); }
.topbar {
  height: 56px; background: rgba(255,255,255,0.85); backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border-light);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 var(--space-6); position: sticky; top: 0; z-index: 100;
}
.topbar-left { display: flex; align-items: center; gap: var(--space-3); }
.brand-icon { color: #7C3AED; display: flex; }
.brand-text { font-weight: var(--font-weight-semibold); color: var(--color-foreground); font-size: 15px; }
.topbar-right { display: flex; align-items: center; gap: var(--space-4); }
.user-chip { display: flex; align-items: center; gap: var(--space-2); }
.user-avatar {
  width: 30px; height: 30px; border-radius: var(--radius-full);
  background: rgba(124, 58, 237, 0.08); color: #7C3AED;
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
.main-content { max-width: 1000px; margin: 0 auto; padding: var(--space-6) var(--space-4); }
</style>
