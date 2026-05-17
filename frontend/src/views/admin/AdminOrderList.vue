<template>
  <div class="order-list-page">
    <div class="page-header">
      <div>
        <h2>排单列表</h2>
        <p class="page-desc">{{ orders.length }} 个排单</p>
      </div>
    </div>

    <div class="filter-bar">
      <div class="search-wrap">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input v-model="keyword" class="search-input" placeholder="搜索用户名..." @keyup.enter="loadOrders" />
      </div>
      <select v-model="statusFilter" class="filter-select" @change="loadOrders">
        <option value="">全部状态</option>
        <option value="WAITING">等待中</option>
        <option value="CURRENT">当前排单</option>
        <option value="PENDING_SETTLEMENT">待结算</option>
        <option value="COMPLETED">已完结</option>
      </select>
      <div class="sort-tabs">
        <button class="sort-tab" :class="{ active: !sortOrder }" @click="sortOrder = false; loadOrders()">从新到旧</button>
        <button class="sort-tab" :class="{ active: sortOrder }" @click="sortOrder = true; loadOrders()">从旧到新</button>
      </div>
    </div>

    <div class="date-row">
      <input type="date" v-model="startDate" class="date-input" @change="loadOrders" />
      <span class="date-sep">至</span>
      <input type="date" v-model="endDate" class="date-input" @change="loadOrders" />
    </div>

    <div v-if="orders.length" class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card" :class="{ completed: order.status === 'COMPLETED' }">
        <div class="order-main">
          <div class="order-user">
            <span class="user-avatar-sm">{{ maskName(order.nickname)[0] }}</span>
            <div>
              <div class="order-name">{{ maskName(order.nickname) }}</div>
              <div class="order-time">{{ formatTime(order.createdAt) }}</div>
            </div>
          </div>
          <div class="order-meta">
            <select class="status-select" :class="'s-' + order.status.toLowerCase()" :value="order.status" @change="handleStatusChange(order.id, ($event.target as HTMLSelectElement).value)">
              <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
            <span v-if="order.totalPrice" class="order-price">¥{{ order.totalPrice }}</span>
          </div>
        </div>
        <div class="order-actions">
          <router-link :to="`/admin/orders/${order.id}`" class="view-link">详情</router-link>
          <button class="btn-delete-sm" @click.stop="handleDelete(order.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2" opacity="0.25"><rect x="3" y="3" width="18" height="18" rx="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="9" y1="21" x2="9" y2="9"/></svg>
      <p>暂无排单</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrders, updateOrderStatus, adminDeleteOrder } from '../../api/admin'
import type { OrderItem } from '../../api/user'
import { maskName, statusOptions } from '../../utils'

const orders = ref<OrderItem[]>([])
const keyword = ref('')
const statusFilter = ref('')
const startDate = ref('')
const endDate = ref('')
const sortOrder = ref(false)

onMounted(() => loadOrders())
async function loadOrders() {
  orders.value = await getAdminOrders({
    keyword: keyword.value || undefined,
    status: statusFilter.value || undefined,
    startDate: startDate.value || undefined,
    endDate: endDate.value || undefined,
    asc: sortOrder.value,
  })
}
async function handleStatusChange(orderId: number, status: string) {
  try { await updateOrderStatus(orderId, status); ElMessage.success('状态已更新') }
  catch { loadOrders() }
}
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该排单吗？', '确认删除', { type: 'warning' })
    await adminDeleteOrder(id)
    ElMessage.success('已删除')
    loadOrders()
  } catch {}
}
function formatTime(t: string | undefined): string {
  if (!t) return '-'
  return t.substring(0, 16).replace('T', ' ')
}
</script>

<style scoped>
.order-list-page { max-width: 900px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-5); }
.page-header h2 { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); }
.page-desc { color: var(--color-text-secondary); font-size: var(--font-size-sm); margin-top: var(--space-1); }
.filter-bar { display: flex; gap: var(--space-3); margin-bottom: var(--space-3); flex-wrap: wrap; }
.search-wrap { display: flex; align-items: center; gap: var(--space-2); padding: 0 var(--space-3); background: var(--color-surface); border: 1px solid var(--color-border); border-radius: var(--radius-md); flex: 1; min-width: 160px; color: var(--color-text-muted); }
.search-input { border: none; outline: none; padding: 8px 0; flex: 1; font-size: var(--font-size-sm); background: transparent; color: var(--color-foreground); font-family: var(--font-sans); }
.search-input::placeholder { color: var(--color-text-muted); }
.filter-select { padding: 8px 12px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text); font-size: var(--font-size-sm); font-family: var(--font-sans); cursor: pointer; outline: none; }
.sort-tabs { display: flex; border-radius: var(--radius-md); overflow: hidden; border: 1px solid var(--color-border); }
.sort-tab { padding: 8px 14px; border: none; background: var(--color-surface); color: var(--color-text-secondary); font-size: var(--font-size-sm); cursor: pointer; font-family: var(--font-sans); transition: all var(--transition-fast); }
.sort-tab.active { background: var(--color-primary); color: #FFF; }
.sort-tab + .sort-tab { border-left: 1px solid var(--color-border); }
.date-row { display: flex; align-items: center; gap: var(--space-2); margin-bottom: var(--space-5); }
.date-input { padding: 6px 10px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; }
.date-sep { color: var(--color-text-muted); font-size: var(--font-size-sm); }
.order-list { display: flex; flex-direction: column; gap: var(--space-2); }
.order-card { display: flex; align-items: center; justify-content: space-between; gap: var(--space-4); padding: var(--space-4) var(--space-5); background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-xs); border: 1px solid var(--color-border-light); transition: all var(--transition-fast); }
.order-card:hover { box-shadow: var(--shadow-md); }
.order-card.completed { opacity: 0.5; background: var(--color-bg); }
.order-main { flex: 1; display: flex; align-items: center; gap: var(--space-6); }
.order-user { display: flex; align-items: center; gap: var(--space-3); min-width: 0; }
.user-avatar-sm { width: 34px; height: 34px; border-radius: var(--radius-full); background: var(--color-primary-bg); color: var(--color-primary); display: flex; align-items: center; justify-content: center; font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); flex-shrink: 0; }
.order-name { font-weight: var(--font-weight-medium); color: var(--color-text); font-size: var(--font-size-sm); }
.order-time { font-size: var(--font-size-xs); color: var(--color-text-muted); margin-top: 2px; }
.order-meta { display: flex; align-items: center; gap: var(--space-3); }
.status-select { padding: 4px 10px; border-radius: var(--radius-full); border: none; font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); cursor: pointer; outline: none; font-family: var(--font-sans); }
.s-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.s-current { background: var(--color-current-bg); color: #16A34A; }
.s-pending_settlement { background: var(--color-pending-bg); color: #D97706; }
.s-completed { background: var(--color-completed-bg); color: #6B7280; }
.order-price { font-weight: var(--font-weight-bold); color: var(--color-accent); font-size: var(--font-size-sm); white-space: nowrap; }
.order-actions { display: flex; align-items: center; gap: var(--space-3); }
.view-link { display: flex; align-items: center; gap: 4px; color: var(--color-primary); text-decoration: none; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); transition: opacity var(--transition-fast); }
.view-link:hover { opacity: 0.8; }
.btn-delete-sm { padding: 4px 12px; border: none; border-radius: var(--radius-sm); background: var(--color-destructive-bg); color: var(--color-destructive); font-size: var(--font-size-xs); cursor: pointer; font-family: var(--font-sans); transition: all var(--transition-fast); }
.btn-delete-sm:hover { background: var(--color-destructive); color: #FFF; }
.empty-state { text-align: center; padding: var(--space-16); color: var(--color-text-muted); }
.empty-state p { margin-top: var(--space-3); font-size: var(--font-size-sm); }
@media (max-width: 640px) {
  .filter-bar { flex-direction: column; }
  .search-wrap { min-width: auto; }
  .sort-tabs { width: 100%; }
  .sort-tab { flex: 1; text-align: center; }
  .order-card { flex-direction: column; align-items: flex-start; gap: var(--space-3); padding: var(--space-3) var(--space-4); }
  .order-main { flex-direction: column; gap: var(--space-2); width: 100%; }
  .order-meta { width: 100%; justify-content: space-between; }
  .order-actions { width: 100%; justify-content: flex-end; }
}
</style>
