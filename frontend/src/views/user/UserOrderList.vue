<template>
  <div class="order-list-page">
    <div class="page-header">
      <button class="btn-back" @click="$router.push('/user/home')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <h2>我的排单</h2>
      <span class="count-badge">{{ orders.length }} 项</span>
    </div>

    <div v-if="orders.length" class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card" :class="'card-' + order.status.toLowerCase()" @click="$router.push(`/user/order/${order.id}`)">
        <div class="order-left">
          <span class="order-id">#{{ order.id }}</span>
          <span class="order-status" :class="'s-' + order.status.toLowerCase()">
            <span class="status-dot"></span>
            {{ statusMap[order.status]?.text || order.status }}
          </span>
        </div>
        <div class="order-right">
          <button v-if="order.status === 'WAITING'" class="btn-action btn-delete" @click.stop="handleDelete(order.id)">删除</button>
          <span class="order-time">{{ order.createdAt?.substring(0, 16)?.replace('T', ' ') }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
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
import { getUserOrders, deleteUserOrder, type OrderItem } from '../../api/user'
import { statusMap } from '../../utils'

const orders = ref<OrderItem[]>([])

onMounted(async () => { orders.value = await getUserOrders() })

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该排单吗？', '确认删除', { type: 'warning' })
    await deleteUserOrder(id)
    ElMessage.success('已删除')
    orders.value = orders.value.filter(o => o.id !== id)
  } catch { /* cancelled */ }
}
</script>

<style scoped>
.order-list-page { max-width: 640px; margin: 0 auto; }
.page-header { display: flex; align-items: center; gap: var(--space-4); margin-bottom: var(--space-6); }
.btn-back { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: var(--radius-md); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); }
.btn-back:hover { background: var(--color-bg); color: var(--color-text); }
.page-header h2 { font-size: var(--font-size-xl); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.count-badge { font-size: var(--font-size-xs); color: var(--color-text-muted); background: var(--color-bg); padding: 2px 10px; border-radius: var(--radius-full); }

.order-list { display: flex; flex-direction: column; gap: var(--space-2); }
.order-card {
  display: flex; align-items: center; justify-content: space-between;
  padding: var(--space-4) var(--space-5); background: var(--color-surface);
  border-radius: var(--radius-lg); box-shadow: var(--shadow-xs);
  border: 1px solid var(--color-border-light); cursor: pointer;
  transition: all var(--transition-fast);
}
.order-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }
.order-card.card-completed { opacity: 0.45; }
.order-left { display: flex; align-items: center; gap: var(--space-3); }
.order-id { font-weight: var(--font-weight-semibold); color: var(--color-text); }
.order-status { display: flex; align-items: center; gap: 6px; font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); padding: 4px 10px; border-radius: var(--radius-full); }
.s-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.s-current { background: var(--color-current-bg); color: #16A34A; }
.s-pending_settlement { background: var(--color-pending-bg); color: #D97706; }
.s-completed { background: var(--color-completed-bg); color: #6B7280; }
.status-dot { width: 5px; height: 5px; border-radius: 50%; background: currentColor; }
.order-right { display: flex; align-items: center; gap: var(--space-2); }
.order-time { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.btn-action { padding: 4px 10px; border-radius: var(--radius-sm); font-size: var(--font-size-xs); cursor: pointer; font-family: var(--font-sans); border: none; transition: all var(--transition-fast); }
.btn-delete { background: var(--color-destructive-bg); color: var(--color-destructive); }
.btn-delete:hover { background: var(--color-destructive); color: #FFF; }

.empty-state { text-align: center; padding: var(--space-16); color: var(--color-text-muted); }
.empty-state p { margin-top: var(--space-3); font-size: var(--font-size-sm); }
@media (max-width: 640px) {
  .page-header h2 { font-size: var(--font-size-lg); }
  .order-card { padding: var(--space-3) var(--space-4); flex-wrap: wrap; gap: var(--space-2); }
  .order-time { font-size: 11px; }
}
</style>
