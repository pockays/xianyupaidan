<template>
  <div class="user-home">
    <transition name="card-swap">
      <div v-if="!homeData.orderEnabled && homeData.announcement" class="banner-warning">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        <span>{{ homeData.announcement }}</span>
      </div>
    </transition>

    <div class="bento-grid">
      <div class="bento-card stat-primary">
        <div class="stat-value">{{ homeData.totalOrders }}</div>
        <div class="stat-label">总排单</div>
      </div>
      <div class="bento-card stat-waiting">
        <div class="stat-value">{{ homeData.waitingOrders }}</div>
        <div class="stat-label">等待中</div>
        <div class="stat-dot"></div>
      </div>
      <div class="bento-card stat-current">
        <div class="stat-value">{{ homeData.currentOrders }}</div>
        <div class="stat-label">当前排单</div>
      </div>
      <div class="bento-card stat-action" :class="{ disabled: !homeData.orderEnabled }" @click="handlePlaceOrder">
        <button class="action-btn-main" :disabled="!homeData.orderEnabled">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          点击排单
        </button>
      </div>
    </div>

    <div class="action-bar">
      <router-link to="/user/orders" class="action-btn">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
        查看我的排单
      </router-link>
    </div>

    <!-- All orders list -->
    <div v-if="homeData.recentOrders.length" class="order-section">
      <div class="section-header">
        <h3 class="section-title">所有排单</h3>
        <span class="section-count">{{ homeData.recentOrders.length }} 项</span>
      </div>
      <div class="order-list">
        <div v-for="(order, idx) in homeData.recentOrders" :key="order.id" class="order-item" :class="{ completed: order.status === 'COMPLETED' }">
          <span class="order-pos">{{ idx + 1 }}</span>
          <span class="order-name">{{ maskName(order.nickname) }}</span>
          <span class="order-status" :class="'status-' + order.status.toLowerCase()">
            <span class="status-dot"></span>
            {{ statusMap[order.status]?.text }}
          </span>
          <span class="order-time">{{ order.createdAt?.substring(0, 10) }}</span>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserHome, type UserHomeData } from '../../api/user'
import { maskName, statusMap } from '../../utils'

const router = useRouter()
const homeData = ref<UserHomeData>({ orderEnabled: true, announcement: '', totalOrders: 0, waitingOrders: 0, currentOrders: 0, recentOrders: [] })

onMounted(async () => { homeData.value = await getUserHome() })

function handlePlaceOrder() { router.push('/user/order/new') }
</script>

<style scoped>
.user-home { max-width: 640px; margin: 0 auto; }
.banner-warning { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-4) var(--space-5); background: #FFFBEB; border: 1px solid #FDE68A; border-radius: var(--radius-md); color: #92400E; font-size: var(--font-size-sm); margin-bottom: var(--space-6); }
.bento-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-4); margin-bottom: var(--space-5); }
.bento-card { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-6); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); position: relative; overflow: hidden; transition: all var(--transition-base); }
.bento-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }
.stat-value { font-size: var(--font-size-3xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); letter-spacing: -1px; }
.stat-label { font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-top: var(--space-1); }
.stat-dot { position: absolute; top: 16px; right: 16px; width: 8px; height: 8px; border-radius: 50%; background: var(--color-current); animation: pulse 2s ease-in-out infinite; }
@keyframes pulse { 0%,100% { opacity: 0.4; } 50% { opacity: 1; } }
.stat-primary { background: linear-gradient(135deg, #FFF 0%, #F1F5FD 100%); }
.stat-waiting { background: linear-gradient(135deg, #FFF 0%, #EBF5FF 100%); }
.stat-current { background: linear-gradient(135deg, #FFF 0%, #F0FDF4 100%); }
.stat-action { display: flex; align-items: center; justify-content: center; padding: var(--space-4); background: var(--color-primary); }
.stat-action.disabled { background: var(--color-text-muted); }
.action-btn-main { display: flex; align-items: center; gap: var(--space-3); background: none; border: none; color: #FFF; font-size: var(--font-size-lg); font-weight: var(--font-weight-bold); cursor: pointer; font-family: var(--font-sans); padding: var(--space-2) var(--space-4); border-radius: var(--radius-md); transition: all var(--transition-fast); }
.action-btn-main:hover:not(:disabled) { background: rgba(255,255,255,0.15); }
.action-btn-main:disabled { opacity: 0.5; cursor: not-allowed; }
.action-bar { display: flex; gap: var(--space-3); margin-top: var(--space-4); }
.action-btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-5); border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text-secondary); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-sans); text-decoration: none; }
.action-btn:hover { background: var(--color-primary-bg); color: var(--color-primary); border-color: var(--color-primary); }
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.3); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 200; }
.dialog { background: var(--color-surface); border-radius: var(--radius-xl); padding: var(--space-8); box-shadow: var(--shadow-xl); max-width: 380px; width: 90vw; }
.dialog h4 { font-size: var(--font-size-lg); font-weight: var(--font-weight-semibold); margin-bottom: var(--space-3); color: var(--color-foreground); }
.dialog p { color: var(--color-text-secondary); font-size: var(--font-size-sm); margin-bottom: var(--space-6); }
.dialog-actions { display: flex; gap: var(--space-3); justify-content: flex-end; }
.btn-cancel { padding: 8px 20px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; font-family: var(--font-sans); font-size: var(--font-size-sm); }
.btn-confirm { padding: 8px 20px; border: none; border-radius: var(--radius-md); background: var(--color-primary); color: #FFF; cursor: pointer; font-family: var(--font-sans); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); display: flex; align-items: center; gap: var(--space-2); }
.btn-confirm:hover { background: var(--color-primary-dark); }
.spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #FFF; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.order-section { margin-top: var(--space-6); background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); overflow: hidden; }
.section-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--color-border-light); }
.section-title { font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.section-count { font-size: var(--font-size-xs); color: var(--color-text-muted); background: var(--color-bg); padding: 2px 8px; border-radius: var(--radius-full); }
.order-list { }
.order-item { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-5); border-bottom: 1px solid var(--color-border-light); transition: background var(--transition-fast); }
.order-item:last-child { border-bottom: none; }
.order-item:hover { background: var(--color-surface-hover); }
.order-item.completed { opacity: 0.45; }
.order-pos { width: 24px; text-align: center; font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); }
.order-name { flex: 1; font-weight: var(--font-weight-medium); color: var(--color-text); }
.order-status { display: flex; align-items: center; gap: 6px; font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); padding: 4px 10px; border-radius: var(--radius-full); }
.status-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.status-current { background: var(--color-current-bg); color: #16A34A; }
.status-pending_settlement { background: var(--color-pending-bg); color: #D97706; }
.status-completed { background: var(--color-completed-bg); color: var(--color-text-muted); }
.status-dot { width: 5px; height: 5px; border-radius: 50%; background: currentColor; }
.order-time { font-size: var(--font-size-xs); color: var(--color-text-muted); width: 80px; text-align: right; }
.card-swap-enter-active, .card-swap-leave-active { transition: all var(--transition-base); }
.card-swap-enter-from, .card-swap-leave-to { opacity: 0; transform: translateY(-4px); }
@media (max-width: 640px) {
  .bento-grid { grid-template-columns: 1fr; gap: var(--space-3); }
  .bento-card { padding: var(--space-4); }
  .stat-value { font-size: 1.5rem; }
  .action-btn-main { font-size: var(--font-size-base); }
  .order-item { flex-wrap: wrap; gap: var(--space-2); padding: var(--space-2) var(--space-4); }
  .order-time { width: auto; }
}
</style>
