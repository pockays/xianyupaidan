<template>
  <transition name="fade">
    <div v-if="visible" class="lightbox-overlay" @click.self="close">
      <button class="lightbox-close" @click="close">&times;</button>
      <button v-if="images.length > 1" class="lightbox-prev" @click="prev">&lsaquo;</button>
      <img :src="images[current]" class="lightbox-img" />
      <button v-if="images.length > 1" class="lightbox-next" @click="next">&rsaquo;</button>
      <span v-if="images.length > 1" class="lightbox-counter">{{ current + 1 }} / {{ images.length }}</span>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ images: string[]; modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [v: boolean] }>()

const visible = ref(false)
const current = ref(0)

watch(() => props.modelValue, (v) => {
  visible.value = v
  if (v && props.images.length) current.value = 0
})

function close() { visible.value = false; emit('update:modelValue', false) }
function prev() { if (current.value > 0) current.value-- }
function next() { if (current.value < props.images.length - 1) current.value++ }
</script>

<style scoped>
.lightbox-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.9); z-index: 9999; display: flex; align-items: center; justify-content: center; }
.lightbox-close { position: absolute; top: 20px; right: 30px; background: none; border: none; color: #FFF; font-size: 36px; cursor: pointer; z-index: 1; }
.lightbox-prev, .lightbox-next { position: absolute; top: 50%; transform: translateY(-50%); background: rgba(255,255,255,0.1); border: none; color: #FFF; font-size: 48px; cursor: pointer; padding: 10px 15px; border-radius: var(--radius-md); }
.lightbox-prev { left: 20px; }
.lightbox-next { right: 20px; }
.lightbox-img { max-width: 90vw; max-height: 90vh; object-fit: contain; }
.lightbox-counter { position: absolute; bottom: 20px; color: rgba(255,255,255,0.6); font-size: var(--font-size-sm); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
