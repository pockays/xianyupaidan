<template>
  <div class="order-editor">
    <div class="editor-header">
      <button class="btn-back" @click="$router.push('/user/orders')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <div class="header-info">
        <h2>排单 #{{ order?.id }}</h2>
        <span v-if="order" class="status-badge" :class="'s-' + order.status.toLowerCase()">{{ statusMap[order.status]?.text }}</span>
      </div>
    </div>

    <div v-if="order" class="editor-body">
      <!-- Permission hint -->
      <div v-if="isCurrent" class="hint-banner">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
        当前排单进行中，仅可添加新分类和项目，不可修改已有内容
      </div>
      <div v-if="isCompleted" class="hint-banner hint-locked">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        此排单已完结，只读模式
      </div>

      <!-- Email -->
      <div class="field-card">
        <label class="field-label">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          联系邮箱 <span class="optional">选填</span>
        </label>
        <input type="email" class="form-input" v-model="order.email" placeholder="输入邮箱地址" :disabled="isReadOnly" />
      </div>

      <!-- Existing categories (read-only in CURRENT mode) -->
      <div v-for="(cat, catIdx) in existingCategories" :key="cat._key" class="category-card">
        <div class="category-head">
          <span class="category-title">{{ cat.categoryName }}</span>
          <button v-if="!isReadOnly" class="btn-icon-delete" @click="removeExistingCategory(catIdx)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
        <div class="items-list">
          <div v-for="(item, itemIdx) in cat.items" :key="itemIdx" class="item-row">
            <span class="item-index">{{ itemIdx + 1 }}</span>
            <div class="item-inputs">
              <input v-model="item.linkUrl" placeholder="输入链接" :disabled="isReadOnly || isCurrent" class="form-input form-input-sm" />
              <textarea v-model="item.note" placeholder="备注" :disabled="isReadOnly || isCurrent" class="form-textarea" rows="1" @input="autoResize($event)" />
            </div>
            <button v-if="!isReadOnly && !isCurrent" class="btn-icon-remove" @click="removeExistingItem(catIdx, itemIdx)" :disabled="cat.items.length <= 1 && itemIdx === 0 && !item.linkUrl && !item.note">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Tag area (hidden if completed) -->
      <div v-if="!isReadOnly" class="tag-field">
        <span class="tag-label">添加分类</span>
        <div class="tag-list">
          <button v-for="tag in presetTags" :key="tag.id" class="tag-chip" :class="{ active: isTagAdded(tag.name) }" @click="addNewCategory(tag.name)">{{ tag.name }}</button>
          <div class="custom-tag-wrap">
            <input v-model="customTag" class="tag-input" placeholder="自定义" size="10" @keyup.enter="addCustomTag" />
          </div>
        </div>
      </div>

      <!-- New categories being added -->
      <transition-group name="cat-list" tag="div" class="categories-wrap">
        <div v-for="(cat, catIdx) in newCategories" :key="cat._key" class="category-card card-new">
          <div class="category-head">
            <span class="category-title">{{ cat.categoryName }}</span>
            <button class="btn-icon-delete" @click="removeNewCategory(catIdx)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
            </button>
          </div>
          <div class="items-list">
            <div v-for="(item, itemIdx) in cat.items" :key="itemIdx" class="item-row">
              <span class="item-index">{{ itemIdx + 1 }}</span>
              <div class="item-inputs">
                <input v-model="item.linkUrl" placeholder="输入链接" class="form-input form-input-sm" @blur="checkAutoAddNew(catIdx, itemIdx)" />
                <textarea v-model="item.note" placeholder="备注" class="form-textarea" rows="1" @input="autoResize($event)" />
              </div>
              <button class="btn-icon-remove" @click="removeNewItem(catIdx, itemIdx)" :disabled="cat.items.length <= 1 && itemIdx === 0 && !item.linkUrl && !item.note">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </div>
          </div>
        </div>
      </transition-group>

      <!-- Actions -->
      <div v-if="!isReadOnly" class="editor-footer">
        <button v-if="order?.status === 'WAITING'" class="btn-danger" :disabled="saving || submitting" @click="handleDelete">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          删除排单
        </button>
        <button class="btn-secondary" :disabled="saving" @click="handleSave">
          <span v-if="saving" class="spinner"></span>
          <span v-else>保存</span>
        </button>
        <button class="btn-primary" :disabled="submitting" @click="handleSubmit">
          <span v-if="submitting" class="spinner"></span>
          <span v-else>提交排单</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrderDetail, updateUserOrder, submitOrder, deleteUserOrder, type OrderDetail, type CategoryDetail, type CreateOrderData } from '../../api/user'
import request from '../../api/request'
import type { PresetTag } from '../../api/admin'
import { statusMap } from '../../utils'

const route = useRoute()
const router = useRouter()
const orderId = Number(route.params.id)

const order = ref<OrderDetail | null>(null)
const existingCategories = ref<(CategoryDetail & { _key?: string })[]>([])
const newCategories = ref<(CategoryDetail & { _key?: string })[]>([])
const presetTags = ref<PresetTag[]>([])
const customTag = ref('')
const saving = ref(false)
const submitting = ref(false)

const isCompleted = computed(() => order.value?.status === 'COMPLETED')
const isCurrent = computed(() => order.value?.status === 'CURRENT')
const isReadOnly = computed(() => isCompleted.value)

onMounted(async () => {
  const data = await getUserOrderDetail(orderId)
  order.value = data
  existingCategories.value = (data.categories || []).map(c => ({ ...c, _key: 'cat_' + c.id + '_' + Date.now() }))
  const tid = localStorage.getItem('tenantId') || 'default'
  try { presetTags.value = await request.get('/public/tags', { params: { tenantId: tid } }) as any }
  catch {
    presetTags.value = [{ id: 1, name: '衣服', sortOrder: 1 },{ id: 2, name: '头发', sortOrder: 2 },{ id: 3, name: '插件', sortOrder: 3 },{ id: 4, name: '饰品', sortOrder: 4 },{ id: 5, name: '妆容', sortOrder: 5 },{ id: 6, name: '表情动作', sortOrder: 6 }]
  }
})

const allCatNames = computed(() => [...existingCategories.value, ...newCategories.value].map(c => c.categoryName))
function isTagAdded(name: string) { return allCatNames.value.includes(name) }

function addNewCategory(name: string) {
  if (isTagAdded(name)) return
  newCategories.value.push({ id: 0, categoryName: name, sortOrder: 0, items: [{ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: 0 }], _key: 'new_' + Date.now() + Math.random() })
}
function addCustomTag() { const n = customTag.value.trim(); if (n) { addNewCategory(n); customTag.value = '' } }
function removeExistingCategory(idx: number) { existingCategories.value.splice(idx, 1) }
function removeNewCategory(idx: number) { newCategories.value.splice(idx, 1) }
function removeExistingItem(catIdx: number, itemIdx: number) { existingCategories.value[catIdx].items.splice(itemIdx, 1) }
function removeNewItem(catIdx: number, itemIdx: number) { newCategories.value[catIdx].items.splice(itemIdx, 1) }
function checkAutoAddNew(catIdx: number, itemIdx: number) {
  const items = newCategories.value[catIdx].items
  if (itemIdx === items.length - 1 && (items[itemIdx].linkUrl || items[itemIdx].note)) {
    items.push({ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: items.length })
  }
}

function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = el.scrollHeight + 'px'
}

function buildRequest(): CreateOrderData {
  // CURRENT: only send newly added categories to avoid duplication
  const cats = isCurrent.value ? newCategories.value : [...existingCategories.value, ...newCategories.value]
  return {
    email: order.value?.email || '',
    categories: cats
      .filter(c => c.items.some(i => i.linkUrl || i.note))
      .map(c => ({
        categoryName: c.categoryName,
        items: c.items.filter(i => i.linkUrl || i.note).map(i => ({ linkUrl: i.linkUrl, note: i.note })),
      })),
  }
}

async function handleSave() {
  saving.value = true
  try {
    await updateUserOrder(orderId, buildRequest())
    ElMessage.success('保存成功')
    const data = await getUserOrderDetail(orderId)
    order.value = data
    existingCategories.value = (data.categories || []).map(c => ({ ...c, _key: 'cat_' + c.id + '_' + Date.now() }))
    newCategories.value = []
    customTag.value = ''
  } finally { saving.value = false }
}
async function handleSubmit() {
  submitting.value = true
  try {
    const req = buildRequest()
    if (!req.categories.length) {
      ElMessage.warning('排单不能为空，请至少添加一个分类和链接')
      return
    }
    await updateUserOrder(orderId, req)
    await submitOrder(orderId)
    ElMessage.success('提交成功')
    router.push('/user/home')
  } finally { submitting.value = false }
}
async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除该排单吗？', '确认删除', { type: 'warning' })
    await deleteUserOrder(orderId)
    ElMessage.success('已删除')
    router.push('/user/orders')
  } catch { /* cancelled */ }
}
</script>

<style scoped>
.order-editor { max-width: 720px; margin: 0 auto; }
.editor-header { display: flex; align-items: center; gap: var(--space-4); margin-bottom: var(--space-6); }
.btn-back { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: var(--radius-md); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); }
.btn-back:hover { background: var(--color-bg); color: var(--color-text); }
.header-info { display: flex; align-items: center; gap: var(--space-3); }
.header-info h2 { font-size: var(--font-size-xl); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.status-badge { font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); padding: 3px 10px; border-radius: var(--radius-full); }
.s-waiting { background: #EBF5FF; color: #2563EB; }
.s-current { background: #F0FDF4; color: #16A34A; }
.s-completed { background: #F3F4F6; color: #6B7280; }

.editor-body { display: flex; flex-direction: column; gap: var(--space-4); }
.hint-banner { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); background: #FFF7ED; border: 1px solid #FED7AA; border-radius: var(--radius-md); color: #C2410C; font-size: var(--font-size-sm); }
.hint-locked { background: #F3F4F6; border-color: #E5E7EB; color: #6B7280; }

.field-card { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-5); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); }
.field-label { display: flex; align-items: center; gap: var(--space-2); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); color: var(--color-text); margin-bottom: var(--space-3); }
.optional { color: var(--color-text-muted); font-weight: var(--font-weight-normal); font-size: var(--font-size-xs); }

.form-input { width: 100%; padding: 9px 13px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); transition: all var(--transition-fast); outline: none; }
.form-input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
.form-input:disabled { background: #F3F4F6; opacity: 0.6; cursor: not-allowed; }
.form-input::placeholder { color: var(--color-text-muted); }
.form-input-sm { padding: 7px 11px; font-size: var(--font-size-sm); }

.tag-field { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-5); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); }
.tag-label { font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); margin-bottom: var(--space-3); display: block; text-transform: uppercase; letter-spacing: 0.5px; }
.tag-list { display: flex; flex-wrap: wrap; gap: var(--space-2); align-items: center; }
.tag-chip { padding: 6px 14px; border-radius: var(--radius-full); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); font-size: var(--font-size-sm); cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-sans); font-weight: var(--font-weight-medium); }
.tag-chip:hover { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-primary-bg); }
.tag-chip.active { background: var(--color-primary); color: #FFF; border-color: var(--color-primary); }
.tag-input { padding: 6px 12px; border-radius: var(--radius-full); border: 1px dashed var(--color-border); background: var(--color-bg); color: var(--color-text); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; width: 100px; }
.tag-input:focus { border-color: var(--color-primary); border-style: solid; }

.categories-wrap { display: flex; flex-direction: column; gap: var(--space-3); }
.category-card { background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); overflow: hidden; transition: all var(--transition-base); }
.card-new { border-color: var(--color-primary); border-style: dashed; }
.category-head { display: flex; justify-content: space-between; align-items: center; padding: var(--space-3) var(--space-5); background: var(--color-bg); border-bottom: 1px solid var(--color-border-light); }
.category-title { font-weight: var(--font-weight-semibold); color: var(--color-text); font-size: var(--font-size-sm); }
.btn-icon-delete { background: none; border: none; cursor: pointer; color: var(--color-text-muted); padding: 4px; border-radius: var(--radius-sm); display: flex; transition: all var(--transition-fast); }
.btn-icon-delete:hover { color: var(--color-destructive); background: var(--color-destructive-bg); }

.items-list { padding: var(--space-2) var(--space-4) var(--space-3); }
.item-row { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-1) 0; }
.item-index { width: 22px; text-align: center; font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); }
.item-inputs { flex: 1; display: flex; gap: var(--space-2); }
.form-textarea { flex: 1; min-width: 100px; min-height: 32px; padding: 7px 11px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; resize: none; overflow: hidden; line-height: 1.4; field-sizing: content; }
.form-textarea:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
.form-textarea:disabled { background: #F3F4F6; opacity: 0.6; cursor: not-allowed; }
.btn-icon-remove { background: none; border: none; cursor: pointer; color: var(--color-text-muted); padding: 2px; border-radius: var(--radius-sm); display: flex; transition: all var(--transition-fast); }
.btn-icon-remove:hover:not(:disabled) { color: var(--color-destructive); }
.btn-icon-remove:disabled { opacity: 0.2; cursor: not-allowed; }

.editor-footer { display: flex; gap: var(--space-3); justify-content: center; padding-top: var(--space-4); }
.btn-primary, .btn-secondary { padding: 10px 24px; border-radius: var(--radius-md); font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-sans); display: flex; align-items: center; gap: var(--space-2); box-shadow: var(--shadow-sm); }
.btn-primary { background: var(--color-primary); color: #FFF; border: none; }
.btn-primary:hover { background: var(--color-primary-dark); box-shadow: var(--shadow-md); }
.btn-secondary { background: var(--color-surface); color: var(--color-text-secondary); border: 1px solid var(--color-border); }
.btn-secondary:hover { background: var(--color-bg); }
.btn-primary:disabled, .btn-secondary:disabled { opacity: 0.6; cursor: not-allowed; }

.spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #FFF; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.cat-list-enter-active { transition: all var(--transition-base); }
.cat-list-leave-active { transition: all var(--transition-fast); }
.cat-list-enter-from { opacity: 0; transform: translateY(-8px) scale(0.97); }
.cat-list-leave-to { opacity: 0; transform: translateX(-8px); }
.cat-list-move { transition: transform var(--transition-base); }
@media (max-width: 640px) {
  .editor-header h2 { font-size: var(--font-size-base); }
  .field-card, .tag-field, .category-card { padding: var(--space-3); }
  .tag-list { gap: var(--space-1); }
  .tag-chip { padding: 4px 10px; font-size: 12px; }
  .tag-input { width: 80px; font-size: 12px; }
  .item-row { flex-wrap: wrap; }
  .item-inputs { flex-direction: column; gap: var(--space-1); }
  .form-textarea { min-width: auto; }
  .editor-footer { flex-direction: column; }
  .editor-footer button { width: 100%; }
}
</style>
