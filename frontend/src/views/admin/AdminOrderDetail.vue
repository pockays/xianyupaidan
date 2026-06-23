<template>
  <div class="order-detail">
    <div class="detail-header">
      <button class="btn-back" @click="$router.push('/admin/orders')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <div class="header-info">
        <h2>排单 #{{ order?.id }}</h2>
        <span v-if="order" class="status-badge" :class="'s-' + order.status.toLowerCase()">{{ statusMap[order.status]?.text }}</span>
      </div>
      <div class="total-price" v-if="order">
        <span class="price-label">总价</span>
        <span class="price-value">¥{{ computedTotal }}</span>
      </div>
    </div>

    <div v-if="order" class="detail-body">
      <div class="meta-card">
        <div class="meta-item"><span class="meta-label">用户</span><span class="meta-value">{{ order.nickname }}</span></div>
        <div class="meta-item"><span class="meta-label">邮箱</span><span class="meta-value">{{ order.email || '未填写' }}</span></div>
        <div class="meta-item"><span class="meta-label">时间</span><span class="meta-value">{{ order.createdAt?.substring(0, 16)?.replace('T', ' ') }}</span></div>
      </div>

      <!-- Existing categories (fully editable) -->
      <div v-for="(cat, catIdx) in existingCategories" :key="cat.id || cat._key" class="category-card">
        <div class="category-head">
          <span>{{ cat.categoryName }}</span>
          <button class="btn-icon-delete" @click="removeExistingCategory(catIdx)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
        <div class="category-body">
          <div v-for="(item, itemIdx) in cat.items" :key="item.id || item._key" class="item-row">
            <button class="item-check" :class="{ done: item.status === 'COMPLETED' }" @click="handleItemStatus(item)">
              <svg v-if="item.status === 'COMPLETED'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            </button>
            <span class="item-index">{{ itemIdx + 1 }}</span>
            <div class="item-content">
              <input v-model="item.linkUrl" placeholder="输入链接" class="form-input form-input-sm" @blur="handleItemUpdate(item); checkExistingAutoAdd(catIdx, itemIdx)" />
              <textarea v-model="item.note" placeholder="备注" class="form-textarea" rows="1" @blur="handleItemUpdate(item)" @input="autoResize($event)" />
              <ImageUpload :model-value="parseImages(item)" @update:model-value="(v: any) => { updateItemImages(item, v); handleItemUpdate(item) }" @preview="(idx: any) => previewImages(parseImages(item), idx)" />
            </div>
            <div class="item-price-wrap">
              <span class="price-symbol">¥</span>
              <input type="number" class="price-input" :value="item.price" min="0" step="0.01" @change="handlePriceChange(item, ($event.target as HTMLInputElement).value)" @focus="($event.target as HTMLInputElement).select()" />
            </div>
            <button class="btn-icon-remove" @click="removeExistingItem(catIdx, itemIdx)" :disabled="cat.items.length <= 1 && itemIdx === 0 && !item.linkUrl && !item.note">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Tag area for adding -->
      <div class="tag-field">
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
            <span>{{ cat.categoryName }}</span>
            <button class="btn-icon-delete" @click="removeNewCategory(catIdx)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="category-body">
            <div v-for="(item, itemIdx) in cat.items" :key="itemIdx" class="item-row">
              <span class="item-index">{{ itemIdx + 1 }}</span>
              <input v-model="item.linkUrl" placeholder="输入链接" class="form-input" @blur="checkAutoAdd(catIdx, itemIdx)" />
              <input v-model="item.note" placeholder="备注" class="form-input form-input-note" />
              <button class="btn-icon-remove" @click="removeNewItem(catIdx, itemIdx)" :disabled="cat.items.length <= 1 && itemIdx === 0 && !item.linkUrl && !item.note">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </div>
          </div>
        </div>
      </transition-group>

      <!-- Save additions -->
      <div class="editor-footer">
        <button class="btn-primary" :disabled="saving" @click="handleAddItems">
          <span v-if="saving" class="spinner"></span>
          <span v-else>保存添加</span>
        </button>
      </div>
    </div>
    <ImageLightbox v-model="lightboxVisible" :images="lightboxImages" :current="lightboxIndex" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminOrderDetail, updateOrderItem, addOrderCategories } from '../../api/admin'
import type { OrderDetail, CategoryDetail } from '../../api/user'
import request from '../../api/request'
import type { PresetTag } from '../../api/admin'
import { statusMap } from '../../utils'
import ImageLightbox from '../../components/common/ImageLightbox.vue'
import ImageUpload from '../../components/common/ImageUpload.vue'
import { deleteOrderItem as deleteItemApi } from '../../api/admin'

type CatWithKey = CategoryDetail & { _key?: string; items: ItemWithKey[] }
type ItemWithKey = CategoryDetail['items'][number] & { _key?: string }
type NewCat = { _key: string; categoryName: string; items: { linkUrl: string; note: string }[] }

const route = useRoute()
const orderId = Number(route.params.id)
const order = ref<OrderDetail | null>(null)
const existingCategories = ref<CatWithKey[]>([])
const newCategories = ref<NewCat[]>([])
const presetTags = ref<PresetTag[]>([])
const customTag = ref('')
const saving = ref(false)
const lightboxVisible = ref(false)
const lightboxImages = ref<string[]>([])
const lightboxIndex = ref(0)
function parseImages(item: any): string[] { if (!item.imageUrls) return []; if (Array.isArray(item.imageUrls)) return item.imageUrls; try { return JSON.parse(item.imageUrls) } catch { return [] } }
function previewImages(imgs: string[], idx: number) { lightboxImages.value = imgs; lightboxIndex.value = idx; lightboxVisible.value = true }

const computedTotal = computed(() => {
  let t = 0; existingCategories.value.forEach(c => c.items.forEach(i => t += i.price || 0))
  return t.toFixed(2)
})

onMounted(async () => {
  const d = await getAdminOrderDetail(orderId)
  order.value = d
  existingCategories.value = (d.categories || []).map(c => ({ ...c, _key: 'cat_'+c.id, items: c.items.map(i => ({ ...i, _key: 'item_'+i.id })) as ItemWithKey[] }))
  const tid = localStorage.getItem('tenantId') || 'default'
  try { presetTags.value = await request.get('/public/tags', { params: { tenantId: tid } }) as any }
  catch { presetTags.value = [{ id:1,name:'衣服',sortOrder:1},{ id:2,name:'头发',sortOrder:2},{ id:3,name:'插件',sortOrder:3},{ id:4,name:'饰品',sortOrder:4},{ id:5,name:'妆容',sortOrder:5},{ id:6,name:'表情动作',sortOrder:6}] }
})

const allCatNames = computed(() => [...existingCategories.value, ...newCategories.value].map(c => c.categoryName))
function isTagAdded(name: string) { return allCatNames.value.includes(name) }
function addNewCategory(name: string) {
  // 已有分类：直接追加空项
  const exist = existingCategories.value.find(c => c.categoryName === name)
  if (exist) {
    exist.items.push({ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: exist.items.length })
    return
  }
  // 新分类已加过就不再重复
  if (newCategories.value.some(c => c.categoryName === name)) return
  newCategories.value.push({ _key: 'new_'+Date.now()+Math.random(), categoryName: name, items: [{ linkUrl:'', note:'' }] })
}
function addCustomTag() { const n = customTag.value.trim(); if (n) { addNewCategory(n); customTag.value = '' } }
function removeNewCategory(idx: number) { newCategories.value.splice(idx, 1) }
function removeNewItem(catIdx: number, itemIdx: number) { newCategories.value[catIdx].items.splice(itemIdx, 1) }
async function removeExistingCategory(idx: number) {
  const cat = existingCategories.value[idx]
  if (!cat) return
  for (const item of cat.items) {
    if (item.id) try { await deleteItemApi(orderId, item.id) } catch {}
  }
  existingCategories.value.splice(idx, 1)
}
async function removeExistingItem(catIdx: number, itemIdx: number) {
  const item = existingCategories.value[catIdx].items[itemIdx]
  if (item.id) try { await deleteItemApi(orderId, item.id) } catch {}
  existingCategories.value[catIdx].items.splice(itemIdx, 1)
}
function checkExistingAutoAdd(catIdx: number, itemIdx: number) {
  const items = existingCategories.value[catIdx].items
  if (itemIdx === items.length - 1 && (items[itemIdx].linkUrl || items[itemIdx].note)) {
    items.push({ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: items.length, imageUrls: [] } as any)
  }
}
function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement; el.style.height = 'auto'; el.style.height = el.scrollHeight + 'px'
}
function checkAutoAdd(catIdx: number, itemIdx: number) {
  const items = newCategories.value[catIdx].items
  if (itemIdx === items.length - 1 && (items[itemIdx].linkUrl || items[itemIdx].note)) {
    items.push({ linkUrl: '', note: '' })
  }
}

async function handleItemUpdate(item: any) {
  try { await updateOrderItem(orderId, item.id, { linkUrl: item.linkUrl, note: item.note, imageUrls: JSON.stringify(parseImages(item)) }) } catch {}
}
function updateItemImages(item: any, urls: string[]) { item.imageUrls = urls }
async function handleItemStatus(item: any) {
  const ns = item.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED'; item.status = ns
  try { await updateOrderItem(orderId, item.id, { status: ns }) } catch { item.status = item.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED' }
}
async function handlePriceChange(item: any, val: string) {
  const p = parseFloat(val) || 0; item.price = p
  try { await updateOrderItem(orderId, item.id, { price: p }) } catch {}
}
async function handleAddItems() {
  if (!newCategories.value.some(c => c.items.some(i => i.linkUrl || i.note))) { ElMessage.warning('请添加内容'); return }
  saving.value = true
  try {
    await addOrderCategories(orderId, {
      categories: newCategories.value
        .filter(c => c.items.some(i => i.linkUrl || i.note))
        .map(c => ({
          categoryName: c.categoryName,
          items: c.items.filter(i => i.linkUrl || i.note).map(i => ({ linkUrl: i.linkUrl, note: i.note })),
        })),
    })
    ElMessage.success('添加成功')
    newCategories.value = []
    const d = await getAdminOrderDetail(orderId)
    order.value = d
    existingCategories.value = (d.categories || []).map(c => ({ ...c, _key: 'cat_'+c.id, items: c.items.map(i => ({ ...i, _key: 'item_'+i.id })) as ItemWithKey[] }))
  } finally { saving.value = false }
}
</script>

<style scoped>
.order-detail { max-width: 800px; margin: 0 auto; }
.detail-header { display: flex; align-items: center; gap: var(--space-4); margin-bottom: var(--space-6); flex-wrap: wrap; }
.btn-back { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: var(--radius-md); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); }
.btn-back:hover { background: var(--color-bg); color: var(--color-text); }
.header-info { display: flex; align-items: center; gap: var(--space-3); }
.header-info h2 { font-size: var(--font-size-xl); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.status-badge { font-size: var(--font-size-xs); font-weight: var(--font-weight-medium); padding: 3px 10px; border-radius: var(--radius-full); }
.s-waiting { background: var(--color-waiting-bg); color: #2563EB; }
.s-current { background: var(--color-current-bg); color: #16A34A; }
.s-pending_settlement { background: var(--color-pending-bg); color: #D97706; }
.s-completed { background: var(--color-completed-bg); color: #6B7280; }
.total-price { margin-left: auto; display: flex; align-items: baseline; gap: var(--space-2); }
.price-label { font-size: var(--font-size-sm); color: var(--color-text-secondary); }
.price-value { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-accent); }
.detail-body { display: flex; flex-direction: column; gap: var(--space-4); }
.meta-card { display: flex; gap: var(--space-8); background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-4) var(--space-5); box-shadow: var(--shadow-xs); border: 1px solid var(--color-border-light); flex-wrap: wrap; }
.meta-item { display: flex; flex-direction: column; gap: 2px; }
.meta-label { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.meta-value { font-size: var(--font-size-sm); color: var(--color-text); font-weight: var(--font-weight-medium); }

.category-card { background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-xs); border: 1px solid var(--color-border-light); overflow: hidden; }
.card-new { border-color: var(--color-primary); border-style: dashed; }
.category-head { padding: var(--space-2) var(--space-5); background: var(--color-bg); font-weight: var(--font-weight-semibold); color: var(--color-text); font-size: var(--font-size-sm); border-bottom: 1px solid var(--color-border-light); display: flex; justify-content: space-between; align-items: center; }
.category-body { padding: var(--space-1) var(--space-4); }
.item-row { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-1) 0; }
.item-check { width: 28px; height: 28px; border-radius: var(--radius-sm); border: 2px solid var(--color-border); background: var(--color-surface); cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all var(--transition-fast); flex-shrink: 0; color: transparent; }
.item-check:hover { border-color: var(--color-accent); }
.item-check.done { background: var(--color-accent); border-color: var(--color-accent); color: #FFF; }
.item-content { flex: 1; display: flex; gap: var(--space-2); min-width: 0; align-items: flex-start; flex-wrap: wrap; }
.form-input-sm { padding: 6px 10px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; min-width: 100px; }
.form-input-sm:focus { border-color: var(--color-primary); }
.form-textarea { flex: 1; min-width: 80px; min-height: 28px; padding: 5px 10px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; resize: none; line-height: 1.4; field-sizing: content; }
.form-textarea:focus { border-color: var(--color-primary); }
.item-link { flex: 2; font-size: var(--font-size-sm); color: var(--color-primary); word-break: break-all; }
.item-note { flex: 1; font-size: var(--font-size-sm); color: var(--color-text-secondary); word-break: break-all; line-height: 1.4; }
.item-images { display: flex; gap: 4px; flex-wrap: wrap; margin-top: 4px; }
.item-thumb { width: 40px; height: 40px; border-radius: var(--radius-sm); object-fit: cover; cursor: pointer; border: 1px solid var(--color-border); }
.item-thumb:hover { border-color: var(--color-primary); }
.item-price-wrap { display: flex; align-items: center; gap: 2px; flex-shrink: 0; background: var(--color-bg); border-radius: var(--radius-sm); padding: 2px 6px; }
.price-symbol { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.price-input { width: 70px; text-align: right; border: none; background: transparent; font-size: var(--font-size-sm); color: var(--color-accent); font-weight: var(--font-weight-semibold); font-family: var(--font-mono); outline: none; padding: 4px 0; }
.price-input:focus { color: var(--color-text); }

.item-index { width: 22px; text-align: center; font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); }
.form-input { flex: 2; padding: 7px 11px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; color: var(--color-foreground); }
.form-input:focus { border-color: var(--color-primary); }
.form-input-note { flex: 1; }
.btn-icon-remove, .btn-icon-delete { background: none; border: none; cursor: pointer; color: var(--color-text-muted); padding: 4px; border-radius: var(--radius-sm); display: flex; transition: all var(--transition-fast); }
.btn-icon-remove:hover:not(:disabled), .btn-icon-delete:hover { color: var(--color-destructive); background: var(--color-destructive-bg); }
.btn-icon-remove:disabled { opacity: 0.2; cursor: not-allowed; }

.tag-field { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-5); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); }
.tag-label { font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); margin-bottom: var(--space-3); display: block; text-transform: uppercase; letter-spacing: 0.5px; }
.tag-list { display: flex; flex-wrap: wrap; gap: var(--space-2); align-items: center; }
.tag-chip { padding: 6px 14px; border-radius: var(--radius-full); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); font-size: var(--font-size-sm); cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-sans); font-weight: var(--font-weight-medium); }
.tag-chip:hover { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-primary-bg); }
.tag-chip.active { background: var(--color-primary); color: #FFF; border-color: var(--color-primary); }
.tag-input { padding: 6px 12px; border-radius: var(--radius-full); border: 1px dashed var(--color-border); background: var(--color-bg); color: var(--color-text); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; width: 100px; }
.tag-input:focus { border-color: var(--color-primary); border-style: solid; }
.categories-wrap { display: flex; flex-direction: column; gap: var(--space-3); }

.editor-footer { display: flex; gap: var(--space-3); justify-content: center; padding-top: var(--space-4); }
.btn-primary { padding: 10px 24px; border-radius: var(--radius-md); font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-sans); display: flex; align-items: center; gap: var(--space-2); box-shadow: var(--shadow-sm); background: var(--color-primary); color: #FFF; border: none; }
.btn-primary:hover { background: var(--color-primary-dark); box-shadow: var(--shadow-md); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #FFF; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.cat-list-enter-active { transition: all var(--transition-base); }
.cat-list-leave-active { transition: all var(--transition-fast); }
.cat-list-enter-from { opacity: 0; transform: translateY(-8px) scale(0.97); }
.cat-list-leave-to { opacity: 0; transform: translateX(-8px); }
.cat-list-move { transition: transform var(--transition-base); }
@media (max-width: 640px) {
  .detail-header { flex-direction: column; align-items: flex-start; gap: var(--space-3); }
  .total-price { margin-left: 0; }
  .meta-card { flex-direction: column; gap: var(--space-3); }
  .item-row { flex-wrap: wrap; }
  .item-content { flex-direction: column; gap: 2px; min-width: 0; }
  .item-link, .item-note { white-space: normal; word-break: break-all; }
  .tag-list { gap: var(--space-1); }
  .tag-chip { padding: 4px 10px; font-size: 12px; }
  .add-input { min-width: 0; }
}
</style>
