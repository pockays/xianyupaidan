<template>
  <div class="admin-home">
    <div class="page-header">
      <h2>管理后台</h2>
      <p class="page-desc">总览排单数据，快速导航</p>
    </div>

    <div class="bento-grid">
      <div class="bento-card stat-waiting">
        <div class="stat-value">{{ stats.waitingOrders }}</div>
        <div class="stat-label">等待中</div>
      </div>
      <div class="bento-card stat-current">
        <div class="stat-value">{{ stats.currentOrders }}</div>
        <div class="stat-label">当前排单</div>
        <div class="stat-pulse"></div>
      </div>
      <div class="bento-card stat-total">
        <div class="stat-value">{{ stats.totalOrders }}</div>
        <div class="stat-label">总排单</div>
      </div>
    </div>

    <div class="nav-cards">
      <router-link to="/admin/orders" class="nav-card">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
        <span>查看排单</span>
      </router-link>
      <router-link to="/admin/manage" class="nav-card">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z"/></svg>
        <span>管理</span>
      </router-link>
    </div>

    <div v-if="recentOrders.length" class="order-section">
      <div class="section-header">
        <h3 class="section-title">进行中排单</h3>
        <span class="section-count">最近 {{ recentOrders.length }} 项</span>
      </div>
      <div class="order-list">
        <div v-for="order in recentOrders" :key="order.id" class="order-item" @click="$router.push(`/admin/orders/${order.id}`)">
          <span class="order-name">{{ maskName(order.nickname) }}</span>
          <select class="status-select" :class="'s-' + order.status.toLowerCase()" :value="order.status"
                  @click.stop @change="handleStatusChange(order.id, ($event.target as HTMLSelectElement).value)">
            <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
          </select>
          <span class="order-time">{{ order.createdAt?.substring(0, 10) }}</span>
          <span v-if="order.totalPrice" class="order-price">¥{{ order.totalPrice }}</span>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="arrow"><polyline points="9 18 15 12 9 6"/></svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminOrders, updateOrderStatus } from '../../api/admin'
import type { OrderItem } from '../../api/user'
import { maskName, statusOptions } from '../../utils'

const stats = ref({ totalOrders: 0, waitingOrders: 0, currentOrders: 0 })
const recentOrders = ref<OrderItem[]>([])

onMounted(async () => { await loadData() })

async function loadData() {
  const orders = await getAdminOrders({})
  stats.value = {
    totalOrders: orders.length,
    waitingOrders: orders.filter(o => o.status === 'WAITING').length,
    currentOrders: orders.filter(o => o.status === 'CURRENT').length,
  }
  recentOrders.value = orders
    .filter(o => o.status !== 'COMPLETED')
    .slice(0, 10)
}

async function handleStatusChange(orderId: number, status: string) {
  try { await updateOrderStatus(orderId, status); await loadData() }
  catch {}
}
</script>

<style scoped>
.admin-home { max-width: 720px; margin: 0 auto; }
.page-header { margin-bottom: var(--space-6); }
.page-header h2 { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); letter-spacing: -0.5px; }
.page-desc { color: var(--color-text-secondary); font-size: var(--font-size-sm); margin-top: var(--space-1); }

.bento-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-4); margin-bottom: var(--space-6); }
.bento-card { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-5); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); position: relative; transition: all var(--transition-base); }
.bento-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }
.stat-value { font-size: var(--font-size-3xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); letter-spacing: -1px; }
.stat-label { font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-top: var(--space-1); }
.stat-pulse { position: absolute; top: 12px; right: 12px; width: 8px; height: 8px; border-radius: 50%; background: var(--color-current); animation: pulse 2s ease-in-out infinite; }
@keyframes pulse { 0%,100%{opacity:0.4;} 50%{opacity:1;} }
.stat-waiting { background: linear-gradient(135deg, #FFF 0%, #EBF5FF 100%); }
.stat-current { background: linear-gradient(135deg, #FFF 0%, #F0FDF4 100%); }
.stat-total { background: linear-gradient(135deg, #FFF 0%, #F1F5FD 100%); }

.nav-cards { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-4); margin-bottom: var(--space-6); }
.nav-card { display: flex; flex-direction: column; align-items: center; gap: var(--space-4); padding: var(--space-8); border-radius: var(--radius-lg); text-decoration: none; background: var(--color-surface); border: 1px solid var(--color-border-light); box-shadow: var(--shadow-sm); color: var(--color-text-secondary); font-weight: var(--font-weight-medium); transition: all var(--transition-base); }
.nav-card:hover { box-shadow: var(--shadow-lg); color: var(--color-primary); border-color: var(--color-primary); transform: translateY(-2px); }

.order-section { background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); overflow: hidden; }
.section-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--color-border-light); }
.section-title { font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.section-count { font-size: var(--font-size-xs); color: var(--color-text-muted); background: var(--color-bg); padding: 2px 8px; border-radius: var(--radius-full); }
.order-item { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-5); border-bottom: 1px solid var(--color-border-light); cursor: pointer; transition: background var(--transition-fast); }
.order-item:last-child { border-bottom: none; }
.order-item:hover { background: var(--color-surface-hover); }
.order-name { flex: 1; font-weight: var(--font-weight-medium); color: var(--color-text); font-size: var(--font-size-sm); }
.order-status { display: flex; align-items: center; gap: 6px; font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); padding: 4px 10px; border-radius: var(--radius-full); }
.status-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.status-current { background: var(--color-current-bg); color: #16A34A; }
.status-pending_settlement { background: var(--color-pending-bg); color: #D97706; }
.status-dot { width: 5px; height: 5px; border-radius: 50%; background: currentColor; }
.order-time { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.order-price { font-weight: var(--font-weight-semibold); color: var(--color-accent); font-size: var(--font-size-sm); }
.arrow { color: var(--color-text-muted); flex-shrink: 0; }
.status-select { padding: 4px 10px; border-radius: var(--radius-full); border: none; font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); cursor: pointer; outline: none; font-family: var(--font-sans); }
.s-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.s-current { background: var(--color-current-bg); color: #16A34A; }
.s-pending_settlement { background: var(--color-pending-bg); color: #D97706; }

@media (max-width: 640px) {
  .bento-grid { grid-template-columns: 1fr; gap: var(--space-3); }
  .nav-cards { grid-template-columns: 1fr; gap: var(--space-3); }
  .bento-card { padding: var(--space-4); }
  .stat-value { font-size: 1.5rem; }
  .order-item { flex-wrap: wrap; gap: var(--space-2); padding: var(--space-2) var(--space-4); }
  .order-item .arrow { display: none; }
}
</style>
