<template>
  <div class="admin-shell">
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/></svg>
        </div>
        <span class="brand-text">管理端</span>
        <nav class="topbar-nav">
          <router-link to="/admin/home" class="nav-link" active-class="nav-link--active">首页</router-link>
          <router-link to="/admin/orders" class="nav-link" active-class="nav-link--active">查看排单</router-link>
          <router-link to="/admin/manage" class="nav-link" active-class="nav-link--active">管理</router-link>
        </nav>
      </div>
      <div class="topbar-right">
        <span class="user-chip">
          <span class="user-avatar">{{ auth.nickname?.[0] || 'A' }}</span>
          <span class="user-name">{{ auth.nickname }}</span>
        </span>
        <button class="btn-logout" @click="handleLogout" title="退出登录">
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
.admin-shell { min-height: 100vh; background: var(--color-bg); }
.topbar {
  height: 56px; background: rgba(255,255,255,0.85); backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border-light);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 var(--space-6); position: sticky; top: 0; z-index: 100;
}
.topbar-left { display: flex; align-items: center; gap: var(--space-4); }
.brand-icon { color: var(--color-accent); display: flex; }
.brand-text { font-weight: var(--font-weight-semibold); color: var(--color-foreground); font-size: 15px; }
.topbar-nav { display: flex; gap: var(--space-1); margin-left: var(--space-4); }
.nav-link {
  padding: 6px 14px; border-radius: var(--radius-md);
  font-size: var(--font-size-sm); color: var(--color-text-secondary);
  text-decoration: none; font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
}
.nav-link:hover { background: var(--color-primary-bg); color: var(--color-primary); }
.nav-link--active { background: var(--color-primary-bg); color: var(--color-primary); }
.topbar-right { display: flex; align-items: center; gap: var(--space-4); }
.user-chip { display: flex; align-items: center; gap: var(--space-2); }
.user-avatar {
  width: 30px; height: 30px; border-radius: var(--radius-full);
  background: var(--color-accent-bg); color: var(--color-accent);
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
.main-content { max-width: 1100px; margin: 0 auto; padding: var(--space-6) var(--space-4); }
@media (max-width: 640px) {
  .topbar-nav { display: none; }
  .main-content { padding: var(--space-4) var(--space-3); }
}
</style>
