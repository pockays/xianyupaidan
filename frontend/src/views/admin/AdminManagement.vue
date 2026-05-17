<template>
  <div class="admin-manage">
    <div class="page-header">
      <h2>系统管理</h2>
    </div>

    <div class="tab-bar">
      <button class="tab-btn" :class="{ active: activeTab === 'tags' }" @click="activeTab = 'tags'">标签管理</button>
      <button class="tab-btn" :class="{ active: activeTab === 'config' }" @click="activeTab = 'config'">系统配置</button>
    </div>

    <!-- Tag management -->
    <div v-show="activeTab === 'tags'" class="panel">
      <div class="panel-head">
        <h3>预设标签</h3>
        <button class="btn-primary-sm" @click="openTagDialog()">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          添加标签
        </button>
      </div>
      <div class="tag-grid">
        <div v-for="tag in tags" :key="tag.id" class="tag-item">
          <span class="tag-name">{{ tag.name }}</span>
          <span class="tag-order">#{{ tag.sortOrder }}</span>
          <div class="tag-actions">
            <button class="btn-edit" @click="openTagDialog(tag)">编辑</button>
            <button class="btn-delete" @click="handleDeleteTag(tag.id)">删除</button>
          </div>
        </div>
      </div>
      <div v-if="!tags.length" class="empty-panel">暂无标签</div>
    </div>

    <!-- System config -->
    <div v-show="activeTab === 'config'" class="panel">
      <div class="config-item">
        <div class="config-info">
          <h4>接单状态</h4>
          <p>关闭后将显示公告并暂停接受排单</p>
        </div>
        <label class="toggle">
          <input type="checkbox" :checked="configForm.orderEnabled === 1" @change="toggleOrderEnabled" />
          <span class="toggle-track">
            <span class="toggle-thumb"></span>
          </span>
          <span class="toggle-label">{{ configForm.orderEnabled ? '接受排单' : '暂停接单' }}</span>
        </label>
      </div>
      <div class="config-item-col">
        <div class="config-info">
          <h4>公告内容</h4>
          <p>暂停接单时展示给用户的内容</p>
        </div>
        <textarea v-model="configForm.announcement" class="config-textarea" rows="3"
                  placeholder="输入公告内容..." @blur="saveConfig"></textarea>
      </div>
    </div>

    <!-- Tag dialog -->
    <transition name="fade">
      <div v-if="showTagDialog" class="overlay">
        <div class="dialog">
          <h4>{{ editingTag ? '编辑标签' : '添加标签' }}</h4>
          <div class="form-group">
            <label class="form-label">标签名</label>
            <input v-model="tagForm.name" class="form-input" placeholder="输入标签名" />
          </div>
          <div class="form-group">
            <label class="form-label">排序</label>
            <input v-model.number="tagForm.sortOrder" type="number" class="form-input" min="0" />
          </div>
          <div class="dialog-actions">
            <button class="btn-cancel" @click="showTagDialog = false">取消</button>
            <button class="btn-primary" @click="handleSaveTag">{{ editingTag ? '保存' : '添加' }}</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTags, createTag, updateTag, deleteTag, getConfig, updateConfig, type PresetTag } from '../../api/admin'

const activeTab = ref('tags')
const tags = ref<PresetTag[]>([])
const showTagDialog = ref(false)
const editingTag = ref<PresetTag | null>(null)
const tagForm = ref({ name: '', sortOrder: 0 })
const configForm = ref({ orderEnabled: 1, announcement: '' })

onMounted(async () => {
  tags.value = await getTags()
  const c = await getConfig()
  configForm.value = { orderEnabled: c.orderEnabled, announcement: c.announcement }
})

function openTagDialog(tag?: PresetTag) {
  editingTag.value = tag || null
  tagForm.value = tag ? { name: tag.name, sortOrder: tag.sortOrder } : { name: '', sortOrder: 0 }
  showTagDialog.value = true
}
async function handleSaveTag() {
  if (!tagForm.value.name) { ElMessage.warning('请输入标签名'); return }
  if (editingTag.value) { await updateTag(editingTag.value.id, tagForm.value); ElMessage.success('更新成功') }
  else { await createTag(tagForm.value); ElMessage.success('添加成功') }
  showTagDialog.value = false; tags.value = await getTags()
}
async function handleDeleteTag(id: number) {
  await ElMessageBox.confirm('确定删除该标签？', '确认', { type: 'warning' })
  await deleteTag(id); ElMessage.success('删除成功'); tags.value = await getTags()
}
async function toggleOrderEnabled(e: Event) {
  const v = (e.target as HTMLInputElement).checked ? 1 : 0
  configForm.value.orderEnabled = v; await saveConfig()
}
async function saveConfig() {
  await updateConfig(configForm.value); ElMessage.success('配置已保存')
}
</script>

<style scoped>
.admin-manage { max-width: 700px; margin: 0 auto; }
.page-header { margin-bottom: var(--space-5); }
.page-header h2 { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); }

/* Tab bar */
.tab-bar { display: flex; gap: var(--space-1); margin-bottom: var(--space-5); background: var(--color-surface); border-radius: var(--radius-lg); padding: 4px; box-shadow: var(--shadow-xs); border: 1px solid var(--color-border-light); }
.tab-btn {
  flex: 1; padding: 8px 16px; border: none; border-radius: var(--radius-md);
  background: transparent; color: var(--color-text-secondary); font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium); cursor: pointer; font-family: var(--font-sans); transition: all var(--transition-fast);
}
.tab-btn.active { background: var(--color-primary); color: #FFF; box-shadow: var(--shadow-sm); }

/* Panel */
.panel { background: var(--color-surface); border-radius: var(--radius-lg); padding: var(--space-6); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); }
.panel-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.panel-head h3 { font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); color: var(--color-foreground); }
.btn-primary-sm {
  display: flex; align-items: center; gap: 6px; padding: 6px 14px;
  border: none; border-radius: var(--radius-md); background: var(--color-primary);
  color: #FFF; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium);
  cursor: pointer; font-family: var(--font-sans); transition: all var(--transition-fast);
}
.btn-primary-sm:hover { background: var(--color-primary-dark); }

/* Tag grid */
.tag-grid { display: flex; flex-direction: column; gap: var(--space-2); }
.tag-item {
  display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-4);
  background: var(--color-bg); border-radius: var(--radius-md); border: 1px solid var(--color-border-light);
  transition: all var(--transition-fast);
}
.tag-item:hover { border-color: var(--color-border); }
.tag-name { font-weight: var(--font-weight-medium); color: var(--color-text); font-size: var(--font-size-sm); flex: 1; }
.tag-order { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.tag-actions { display: flex; gap: var(--space-2); }
.btn-edit, .btn-delete {
  padding: 4px 12px; border-radius: var(--radius-sm); font-size: var(--font-size-xs);
  cursor: pointer; font-family: var(--font-sans); border: none; transition: all var(--transition-fast);
}
.btn-edit { background: var(--color-primary-bg); color: var(--color-primary); }
.btn-edit:hover { background: var(--color-primary); color: #FFF; }
.btn-delete { background: var(--color-destructive-bg); color: var(--color-destructive); }
.btn-delete:hover { background: var(--color-destructive); color: #FFF; }
.empty-panel { text-align: center; padding: var(--space-8); color: var(--color-text-muted); font-size: var(--font-size-sm); }

/* Config items */
.config-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: var(--space-4) 0; border-bottom: 1px solid var(--color-border-light);
}
.config-item-col { padding: var(--space-4) 0; border-bottom: 1px solid var(--color-border-light); }
.config-info h4 { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-text); margin-bottom: 2px; }
.config-info p { font-size: var(--font-size-xs); color: var(--color-text-muted); }
.config-textarea {
  width: 100%; margin-top: var(--space-3); padding: var(--space-3);
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  font-size: var(--font-size-sm); font-family: var(--font-sans); resize: vertical;
  background: var(--color-bg); color: var(--color-foreground); outline: none;
}
.config-textarea:focus { border-color: var(--color-primary); }

/* Toggle */
.toggle { display: flex; align-items: center; gap: var(--space-3); cursor: pointer; }
.toggle input { display: none; }
.toggle-track {
  width: 44px; height: 24px; border-radius: 12px; background: var(--color-border);
  position: relative; transition: background var(--transition-fast);
}
.toggle input:checked + .toggle-track { background: var(--color-accent); }
.toggle-thumb {
  position: absolute; top: 3px; left: 3px; width: 18px; height: 18px;
  border-radius: 50%; background: #FFF; transition: transform var(--transition-fast);
  box-shadow: 0 1px 2px rgba(0,0,0,0.15);
}
.toggle input:checked + .toggle-track .toggle-thumb { transform: translateX(20px); }
.toggle-label { font-size: var(--font-size-sm); color: var(--color-text); }

/* Dialogs */
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.3); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 200; }
.dialog { background: var(--color-surface); border-radius: var(--radius-xl); padding: var(--space-8); box-shadow: var(--shadow-xl); max-width: 400px; width: 90vw; }
.dialog h4 { font-size: var(--font-size-lg); font-weight: var(--font-weight-semibold); margin-bottom: var(--space-5); color: var(--color-foreground); }
.form-group { margin-bottom: var(--space-4); }
.form-label { display: block; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); color: var(--color-text); margin-bottom: var(--space-2); }
.form-input { width: 100%; padding: 9px 13px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; color: var(--color-foreground); }
.form-input:focus { border-color: var(--color-primary); }
.dialog-actions { display: flex; gap: var(--space-3); justify-content: flex-end; margin-top: var(--space-5); }
.btn-cancel { padding: 8px 20px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; font-family: var(--font-sans); font-size: var(--font-size-sm); }
.btn-primary { padding: 8px 20px; border: none; border-radius: var(--radius-md); background: var(--color-primary); color: #FFF; cursor: pointer; font-family: var(--font-sans); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); }
.btn-primary:hover { background: var(--color-primary-dark); }
.fade-enter-active, .fade-leave-active { transition: opacity var(--transition-base); }
.fade-enter-from, .fade-leave-to { opacity: 0; }
@media (max-width: 640px) {
  .tab-bar { flex-direction: column; }
  .panel { padding: var(--space-4); }
  .panel-head { flex-direction: column; gap: var(--space-3); align-items: flex-start; }
  .config-item { flex-direction: column; gap: var(--space-3); align-items: flex-start; }
}
</style>
