<template>
  <div class="order-editor">
    <div class="editor-header">
      <button class="btn-back" @click="$router.push('/user/home')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <div class="header-info">
        <h2>新建排单</h2>
      </div>
    </div>

    <div class="editor-body">
      <div class="field-card">
        <label class="field-label">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          联系邮箱 <span class="optional">选填</span>
        </label>
        <input type="email" class="form-input" v-model="email" placeholder="输入邮箱地址" />
      </div>

      <div class="tag-field">
        <span class="tag-label">添加分类</span>
        <div class="tag-list">
          <button v-for="tag in presetTags" :key="tag.id" class="tag-chip" :class="{ active: isTagAdded(tag.name) }" @click="addCategory(tag.name)">{{ tag.name }}</button>
          <div class="custom-tag-wrap">
            <input v-model="customTag" class="tag-input" placeholder="自定义" size="10" @keyup.enter="addCustomTag" />
          </div>
        </div>
      </div>

      <transition-group name="cat-list" tag="div" class="categories-wrap">
        <div v-for="(cat, catIdx) in categories" :key="cat._key" class="category-card card-new">
          <div class="category-head">
            <span class="category-title">{{ cat.categoryName }}</span>
            <button class="btn-icon-delete" @click="removeCategory(catIdx)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
            </button>
          </div>
          <div class="items-list">
            <div v-for="(item, itemIdx) in cat.items" :key="itemIdx" class="item-row">
              <span class="item-index">{{ itemIdx + 1 }}</span>
              <div class="item-inputs">
                <input v-model="item.linkUrl" placeholder="输入链接" class="form-input form-input-sm" @blur="checkAutoAdd(catIdx, itemIdx)" />
                <textarea v-model="item.note" placeholder="备注" class="form-textarea" rows="1" @input="autoResize($event)" />
                <ImageUpload :model-value="(item as any).imageUrls || []" @update:model-value="(v: any) => (item as any).imageUrls = v" @preview="(idx: any) => previewImages((item as any).imageUrls || [], idx)" />
              </div>
              <button class="btn-icon-remove" @click="removeItem(catIdx, itemIdx)" :disabled="cat.items.length <= 1 && itemIdx === 0 && !item.linkUrl && !item.note">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </div>
          </div>
        </div>
      </transition-group>

      <div class="editor-footer">
        <button class="btn-primary" :disabled="submitting" @click="handleSubmit">
          <span v-if="submitting" class="spinner"></span>
          <span v-else>提交排单</span>
        </button>
      </div>
    </div>
    <ImageLightbox v-model="lightboxVisible" :images="lightboxImages" :current="lightboxIndex" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, submitOrder } from '../../api/user'
import request from '../../api/request'
import type { PresetTag } from '../../api/admin'
import ImageUpload from '../../components/common/ImageUpload.vue'
import ImageLightbox from '../../components/common/ImageLightbox.vue'

const router = useRouter()

interface CatItem { id: number; linkUrl: string; note: string; price: number; status: string; sortOrder: number }
interface Category { _key: string; id: number; categoryName: string; sortOrder: number; items: CatItem[] }

const email = ref('')
const categories = ref<Category[]>([])
const presetTags = ref<PresetTag[]>([])
const customTag = ref('')
const submitting = ref(false)
const lightboxVisible = ref(false)
const lightboxImages = ref<string[]>([])
const lightboxIndex = ref(0)
function previewImages(imgs: string[], idx: number) { lightboxImages.value = imgs; lightboxIndex.value = idx; lightboxVisible.value = true }

onMounted(async () => {
  const tid = localStorage.getItem('tenantId') || 'default'
  try { presetTags.value = await request.get('/public/tags', { params: { tenantId: tid } }) as any }
  catch {
    presetTags.value = [{ id: 1, name: '衣服', sortOrder: 1 },{ id: 2, name: '头发', sortOrder: 2 },{ id: 3, name: '插件', sortOrder: 3 },{ id: 4, name: '饰品', sortOrder: 4 },{ id: 5, name: '妆容', sortOrder: 5 },{ id: 6, name: '表情动作', sortOrder: 6 }]
  }
})

const allCatNames = computed(() => categories.value.map(c => c.categoryName))
function isTagAdded(name: string) { return allCatNames.value.includes(name) }

function addCategory(name: string) {
  if (isTagAdded(name)) return
  categories.value.push({ _key: 'new_' + Date.now() + Math.random(), id: 0, categoryName: name, sortOrder: 0, items: [{ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: 0 }] })
}
function addCustomTag() { const n = customTag.value.trim(); if (n) { addCategory(n); customTag.value = '' } }
function removeCategory(idx: number) { categories.value.splice(idx, 1) }
function removeItem(catIdx: number, itemIdx: number) { categories.value[catIdx].items.splice(itemIdx, 1) }
function checkAutoAdd(catIdx: number, itemIdx: number) {
  const items = categories.value[catIdx].items
  if (itemIdx === items.length - 1 && (items[itemIdx].linkUrl || items[itemIdx].note)) {
    items.push({ id: 0, linkUrl: '', note: '', price: 0, status: 'PENDING', sortOrder: items.length })
  }
}
function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = el.scrollHeight + 'px'
}

function buildRequest() {
  return {
    email: email.value,
    categories: categories.value
      .filter(c => c.items.some(i => i.linkUrl || i.note))
      .map(c => ({
        categoryName: c.categoryName,
        items: c.items.filter(i => i.linkUrl || i.note).map(i => { const urls = Array.isArray((i as any).imageUrls) ? JSON.stringify((i as any).imageUrls) : ''; return { linkUrl: i.linkUrl, note: i.note, imageUrls: urls === '[]' ? '' : urls } }),
      })),
  }
}

async function handleSubmit() {
  const req = buildRequest()
  if (!req.categories.length) {
    ElMessage.warning('排单不能为空，请至少添加一个分类和链接')
    return
  }
  submitting.value = true
  try {
    const orderId = await createOrder(req)
    await submitOrder(orderId)
    ElMessage.success('提交成功')
    router.push('/user/home')
  } finally { submitting.value = false }
}
</script>

<style scoped>
.order-editor { max-width: 720px; margin: 0 auto; }
.editor-header { display: flex; align-items: center; gap: var(--space-4); margin-bottom: var(--space-6); }
.btn-back { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: var(--radius-md); border: 1px solid var(--color-border); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; transition: all var(--transition-fast); }
.btn-back:hover { background: var(--color-bg); color: var(--color-text); }
.header-info h2 { font-size: var(--font-size-xl); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.editor-body { display: flex; flex-direction: column; gap: var(--space-4); }
.field-card { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-5); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); }
.field-label { display: flex; align-items: center; gap: var(--space-2); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); color: var(--color-text); margin-bottom: var(--space-3); }
.optional { color: var(--color-text-muted); font-weight: var(--font-weight-normal); font-size: var(--font-size-xs); }
.form-input { width: 100%; padding: 9px 13px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); transition: all var(--transition-fast); outline: none; }
.form-input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
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
.category-card { background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); overflow: hidden; }
.card-new { border-color: var(--color-primary); border-style: dashed; }
.category-head { display: flex; justify-content: space-between; align-items: center; padding: var(--space-3) var(--space-5); background: var(--color-bg); border-bottom: 1px solid var(--color-border-light); }
.category-title { font-weight: var(--font-weight-semibold); color: var(--color-text); font-size: var(--font-size-sm); }
.btn-icon-delete { background: none; border: none; cursor: pointer; color: var(--color-text-muted); padding: 4px; border-radius: var(--radius-sm); display: flex; transition: all var(--transition-fast); }
.btn-icon-delete:hover { color: var(--color-destructive); background: var(--color-destructive-bg); }
.items-list { padding: var(--space-2) var(--space-4) var(--space-3); }
.item-row { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-1) 0; }
.item-index { width: 22px; text-align: center; font-size: var(--font-size-xs); color: var(--color-text-muted); font-weight: var(--font-weight-medium); }
.item-inputs { flex: 1; display: flex; gap: var(--space-2); flex-wrap: wrap; }
.form-textarea { flex: 1; min-width: 100px; min-height: 32px; padding: 7px 11px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); color: var(--color-foreground); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; resize: none; overflow: hidden; line-height: 1.4; field-sizing: content; }
.form-textarea:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-bg); }
.btn-icon-remove { background: none; border: none; cursor: pointer; color: var(--color-text-muted); padding: 2px; border-radius: var(--radius-sm); display: flex; transition: all var(--transition-fast); }
.btn-icon-remove:hover:not(:disabled) { color: var(--color-destructive); }
.btn-icon-remove:disabled { opacity: 0.2; cursor: not-allowed; }
.editor-footer { display: flex; gap: var(--space-3); justify-content: center; padding-top: var(--space-4); }
.btn-primary { padding: 10px 40px; border-radius: var(--radius-md); background: var(--color-primary); color: #FFF; border: none; font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); cursor: pointer; font-family: var(--font-sans); display: flex; align-items: center; gap: var(--space-2); box-shadow: var(--shadow-sm); }
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
  .editor-header h2 { font-size: var(--font-size-base); }
  .field-card, .tag-field, .category-card { padding: var(--space-3); }
  .tag-list { gap: var(--space-1); }
  .tag-chip { padding: 4px 10px; font-size: 12px; }
  .tag-input { width: 80px; font-size: 12px; }
  .item-row { flex-wrap: wrap; }
  .item-inputs { flex-direction: column; gap: var(--space-1); }
  .form-textarea { min-width: auto; }
}
</style>
