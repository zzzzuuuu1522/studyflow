<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'

const props = defineProps({
  record: { type: Object, required: true },
  tasks: { type: Array, required: true },
  saving: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'save'])
const validationMessage = ref('')
const form = reactive({
  taskId: props.record.taskId,
  studyDate: props.record.studyDate,
  durationMinutes: props.record.durationMinutes,
  note: props.record.note || '',
})

function save() {
  const minutes = Number(form.durationMinutes)
  if (!form.taskId) {
    validationMessage.value = '请选择关联任务'
    return
  }
  if (!Number.isInteger(minutes) || minutes < 1 || minutes > 1440) {
    validationMessage.value = '学习时长必须是 1 到 1440 之间的整数'
    return
  }
  emit('save', {
    taskId: Number(form.taskId),
    studyDate: form.studyDate,
    durationMinutes: minutes,
    note: form.note.trim() || null,
  })
}

function handleKeydown(event) {
  if (event.key === 'Escape' && !props.saving) emit('close')
}

onMounted(() => window.addEventListener('keydown', handleKeydown))
onBeforeUnmount(() => window.removeEventListener('keydown', handleKeydown))
</script>

<template>
  <div class="modal-backdrop" @mousedown.self="!saving && emit('close')">
    <section class="modal" role="dialog" aria-modal="true" aria-labelledby="record-edit-title">
      <div class="modal-header">
        <div>
          <span class="eyebrow">EDIT STUDY RECORD</span>
          <h2 id="record-edit-title">编辑学习记录</h2>
        </div>
        <button class="close-button" type="button" aria-label="关闭" :disabled="saving" @click="emit('close')">×</button>
      </div>

      <form class="task-form" @submit.prevent="save">
        <label class="field field-wide">
          <span>关联任务 <b>*</b></span>
          <select v-model="form.taskId" required>
            <option v-for="task in tasks" :key="task.id" :value="task.id">{{ task.title }}</option>
          </select>
        </label>
        <label class="field">
          <span>学习日期 <b>*</b></span>
          <input v-model="form.studyDate" type="date" required />
        </label>
        <label class="field">
          <span>学习时长（分钟）<b>*</b></span>
          <input v-model="form.durationMinutes" type="number" min="1" max="1440" required />
        </label>
        <label class="field field-wide">
          <span>学习备注</span>
          <textarea v-model="form.note" maxlength="500" rows="4"></textarea>
        </label>
        <p v-if="validationMessage" class="study-form-error field-wide" role="alert">{{ validationMessage }}</p>
        <div class="modal-actions field-wide">
          <button class="secondary-button" type="button" :disabled="saving" @click="emit('close')">取消</button>
          <button class="primary-button" type="submit" :disabled="saving">
            {{ saving ? '正在保存…' : '保存修改' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>
