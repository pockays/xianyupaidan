<template>
  <div class="image-upload">
    <div
      class="drop-zone"
      :class="{ dragging: isDragging }"
      @dragover.prevent="isDragging = true"
      @dragleave.prevent="isDragging = false"
      @drop.prevent="handleDrop"
    >
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
      <span class="drop-text">拖入图片</span>
    </div>
    <div v-if="images.length" class="thumb-list">
      <div v-for="(img, idx) in images" :key="idx" class="thumb-item">
        <img :src="img" class="thumb-img" @click="$emit('preview', idx)" />
        <button class="thumb-remove" @click="removeImage(idx)">&times;</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api/request'

const props = defineProps<{ modelValue: string[] }>()
const emit = defineEmits<{ 'update:modelValue': [urls: string[]]; 'preview': [index: number] }>()

const isDragging = ref(false)
function normalize(val: any): string[] { if (!val) return []; if (Array.isArray(val)) return val; try { return JSON.parse(val) } catch { return [] } }
const images = ref<string[]>(normalize(props.modelValue))

async function uploadFiles(files: FileList) {
  const formData = new FormData()
  for (const f of files) {
    if (!f.type.startsWith('image/')) continue
    formData.append('files', f)
  }
  if (!formData.has('files')) return
  try {
    const urls = await request.post('/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } }) as any
    if (urls?.length) {
      images.value.push(...urls)
      emit('update:modelValue', [...images.value])
    }
  } catch { ElMessage.error('上传失败') }
}

function handleDrop(e: DragEvent) {
  isDragging.value = false
  if (e.dataTransfer?.files.length) uploadFiles(e.dataTransfer.files)
}

function removeImage(idx: number) {
  images.value.splice(idx, 1)
  emit('update:modelValue', [...images.value])
}
</script>

<style scoped>
.image-upload { display: flex; align-items: flex-start; gap: var(--space-2); min-width: 80px; }
.drop-zone { width: 40px; height: 32px; border: 1.5px dashed var(--color-border); border-radius: var(--radius-sm); display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: all var(--transition-fast); color: var(--color-text-muted); flex-shrink: 0; }
.drop-zone:hover, .drop-zone.dragging { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-primary-bg); }
.drop-text { font-size: 9px; line-height: 1; margin-top: 1px; }
.thumb-list { display: flex; gap: 4px; flex-wrap: wrap; }
.thumb-item { position: relative; width: 32px; height: 32px; border-radius: var(--radius-sm); overflow: hidden; border: 1px solid var(--color-border); flex-shrink: 0; }
.thumb-img { width: 100%; height: 100%; object-fit: cover; cursor: pointer; }
.thumb-remove { position: absolute; top: -2px; right: -2px; width: 14px; height: 14px; border-radius: 50%; background: var(--color-destructive); color: #FFF; border: none; font-size: 10px; line-height: 1; cursor: pointer; display: flex; align-items: center; justify-content: center; padding: 0; }
</style>
