<template>
  <div class="super-manage">
    <div class="page-header">
      <h2>管理员管理</h2>
      <button class="btn-primary" @click="openCreate()">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        创建管理员
      </button>
    </div>

    <!-- Admin table -->
    <div class="table-wrap">
      <table class="data-table" v-if="admins.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>租户ID</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in admins" :key="admin.id" :class="{ disabled: admin.status === 0 }">
            <td class="col-id">{{ admin.id }}</td>
            <td class="col-name">{{ admin.username }}</td>
            <td class="col-email">{{ admin.email || '-' }}</td>
            <td class="col-tenant"><code>{{ admin.tenantId?.substring(0, 12) }}...</code></td>
            <td>
              <span class="status-dot" :class="admin.status === 1 ? 'active' : 'inactive'"></span>
              {{ admin.status === 1 ? '启用' : '禁用' }}
            </td>
            <td class="col-actions">
              <button class="btn-action btn-edit" @click="openEdit(admin)">编辑</button>
              <button class="btn-action btn-toggle" @click="handleToggleStatus(admin)">
                {{ admin.status === 1 ? '禁用' : '启用' }}
              </button>
              <button class="btn-action btn-delete" @click="handleDelete(admin.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2" opacity="0.25"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        <p>暂无管理员</p>
      </div>
    </div>

    <!-- Dialog -->
    <transition name="fade">
      <div v-if="showDialog" class="overlay">
        <div class="dialog">
          <h4>{{ isEdit ? '编辑管理员' : '创建管理员' }}</h4>
          <div class="form-group">
            <label class="form-label">用户名 <span class="required">*</span></label>
            <input v-model="form.username" class="form-input" placeholder="输入用户名" />
          </div>
          <div class="form-group">
            <label class="form-label">密码 {{ !isEdit ? '*' : '' }} <span v-if="isEdit" class="optional">留空则不修改</span></label>
            <div class="input-wrap">
              <input :type="showPwd ? 'text' : 'password'" v-model="form.password" class="form-input" :placeholder="isEdit ? '留空则不修改' : '输入密码'" />
              <button class="input-suffix" @click="showPwd = !showPwd">
                <svg v-if="showPwd" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8Z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">邮箱</label>
            <input v-model="form.email" class="form-input" placeholder="输入邮箱（用于接收通知）" />
          </div>
          <div class="dialog-actions">
            <button class="btn-cancel" @click="showDialog = false">取消</button>
            <button class="btn-primary" @click="handleSave">{{ isEdit ? '保存' : '创建' }}</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdmins, createAdmin, updateAdmin, deleteAdmin, toggleAdminStatus, type AdminInfo } from '../../api/superAdmin'

const admins = ref<AdminInfo[]>([])
const showDialog = ref(false)
const isEdit = ref(false)
const editingId = ref(0)
const showPwd = ref(false)
const form = ref({ username: '', password: '', email: '' })

onMounted(async () => { admins.value = await getAdmins() })

function openCreate() { isEdit.value = false; editingId.value = 0; form.value = { username: '', password: '', email: '' }; showPwd.value = false; showDialog.value = true }
function openEdit(row: AdminInfo) { isEdit.value = true; editingId.value = row.id; form.value = { username: row.username, password: '', email: row.email || '' }; showPwd.value = false; showDialog.value = true }

async function handleSave() {
  if (!form.value.username) { ElMessage.warning('请输入用户名'); return }
  if (!isEdit.value && !form.value.password) { ElMessage.warning('请输入密码'); return }
  if (isEdit.value) {
    await updateAdmin(editingId.value, { username: form.value.username, password: form.value.password || undefined, email: form.value.email })
    ElMessage.success('更新成功')
  } else {
    await createAdmin(form.value); ElMessage.success('创建成功')
  }
  showDialog.value = false; admins.value = await getAdmins()
}
async function handleToggleStatus(row: AdminInfo) { await toggleAdminStatus(row.id); ElMessage.success('状态已切换'); admins.value = await getAdmins() }
async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该管理员吗？所有相关数据将无法访问', '确认删除', { type: 'warning' })
  await deleteAdmin(id); ElMessage.success('删除成功'); admins.value = await getAdmins()
}
</script>

<style scoped>
.super-manage { max-width: 960px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-6); }
.page-header h2 { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-foreground); }

.btn-primary {
  display: flex; align-items: center; gap: 6px; padding: 8px 18px;
  border: none; border-radius: var(--radius-md); background: #7C3AED;
  color: #FFF; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium);
  cursor: pointer; font-family: var(--font-sans); transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}
.btn-primary:hover { background: #6D28D9; box-shadow: var(--shadow-md); }

/* Table */
.table-wrap { background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); border: 1px solid var(--color-border-light); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { text-align: left; padding: var(--space-3) var(--space-5); font-size: var(--font-size-xs); font-weight: var(--font-weight-semibold); color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 0.5px; background: var(--color-bg); border-bottom: 1px solid var(--color-border-light); }
.data-table td { padding: var(--space-3) var(--space-5); font-size: var(--font-size-sm); color: var(--color-text); border-bottom: 1px solid var(--color-border-light); }
.data-table tr:last-child td { border-bottom: none; }
.data-table tr.disabled { opacity: 0.45; }
.data-table tr:hover td { background: var(--color-surface-hover); }
.col-id { width: 60px; color: var(--color-text-muted) !important; }
.col-name { font-weight: var(--font-weight-medium); }
.col-email { color: var(--color-text-secondary); }
.col-tenant code { font-size: var(--font-size-xs); background: var(--color-bg); padding: 2px 6px; border-radius: var(--radius-sm); color: var(--color-text-muted); }
.col-actions { white-space: nowrap; }
.status-dot { display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 6px; }
.status-dot.active { background: var(--color-accent); }
.status-dot.inactive { background: var(--color-text-muted); }

.btn-action {
  padding: 4px 10px; border-radius: var(--radius-sm); font-size: var(--font-size-xs);
  cursor: pointer; font-family: var(--font-sans); border: none; margin-right: 4px;
  transition: all var(--transition-fast);
}
.btn-edit { background: rgba(124,58,237,0.08); color: #7C3AED; }
.btn-edit:hover { background: #7C3AED; color: #FFF; }
.btn-toggle { background: #FEF3C7; color: #B45309; }
.btn-toggle:hover { background: #F59E0B; color: #FFF; }
.btn-delete { background: var(--color-destructive-bg); color: var(--color-destructive); }
.btn-delete:hover { background: var(--color-destructive); color: #FFF; }

.empty-state { text-align: center; padding: var(--space-16); color: var(--color-text-muted); }
.empty-state p { margin-top: var(--space-3); font-size: var(--font-size-sm); }

/* Dialog */
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.3); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 200; }
.dialog { background: var(--color-surface); border-radius: var(--radius-xl); padding: var(--space-8); box-shadow: var(--shadow-xl); max-width: 430px; width: 90vw; }
.dialog h4 { font-size: var(--font-size-lg); font-weight: var(--font-weight-semibold); margin-bottom: var(--space-5); color: var(--color-foreground); }
.form-group { margin-bottom: var(--space-4); }
.form-label { display: block; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); color: var(--color-text); margin-bottom: var(--space-2); }
.required { color: var(--color-destructive); }
.optional { color: var(--color-text-muted); font-weight: var(--font-weight-normal); font-size: var(--font-size-xs); }
.form-input { width: 100%; padding: 9px 13px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-bg); font-size: var(--font-size-sm); font-family: var(--font-sans); outline: none; color: var(--color-foreground); }
.form-input:focus { border-color: #7C3AED; box-shadow: 0 0 0 3px rgba(124,58,237,0.06); }
.input-wrap { position: relative; }
.input-suffix { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; color: var(--color-text-muted); display: flex; padding: 4px; }
.dialog-actions { display: flex; gap: var(--space-3); justify-content: flex-end; margin-top: var(--space-5); }
.btn-cancel { padding: 8px 20px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); color: var(--color-text-secondary); cursor: pointer; font-family: var(--font-sans); font-size: var(--font-size-sm); }
.fade-enter-active, .fade-leave-active { transition: opacity var(--transition-base); }
.fade-enter-from, .fade-leave-to { opacity: 0; }
@media (max-width: 640px) {
  .page-header { flex-direction: column; gap: var(--space-3); }
  .table-wrap { overflow-x: auto; -webkit-overflow-scrolling: touch; }
  .data-table { min-width: 600px; }
  .data-table th, .data-table td { padding: var(--space-2) var(--space-3); font-size: 12px; }
  .col-actions { white-space: nowrap; }
}
</style>
