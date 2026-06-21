<script setup>
import { reactive, ref } from 'vue'

defineProps({
  tasks: { type: Array, required: true },
  submitting: { type: Boolean, default: false },
})

const emit = defineEmits(['submit'])
const validationMessage = ref('')

function todayString() {
  const now = new Date()
  const offset = now.getTimezoneOffset() * 60_000
  return new Date(now.getTime() - offset).toISOString().slice(0, 10)
}

const initialForm = () => ({
  taskId: '',
  studyDate: todayString(),
  durationMinutes: '',
  note: '',
})

const form = reactive(initialForm())

function submit() {
  const minutes = Number(form.durationMinutes)
  if (!form.taskId) {
    validationMessage.value = '请选择关联任务'
    return
  }
  if (!Number.isInteger(minutes) || minutes < 1 || minutes > 1440) {
    validationMessage.value = '学习时长必须是 1 到 1440 之间的整数'
    return
  }
  validationMessage.value = ''
  emit('submit', {
    taskId: Number(form.taskId),
    studyDate: form.studyDate,
    durationMinutes: minutes,
    note: form.note.trim() || null,
  })
}

function reset() {
  Object.assign(form, initialForm())
  validationMessage.value = ''
}

defineExpose({ reset })
</script>

<template>
  <section class="study-form-panel">
    <div class="section-heading">
      <div>
        <span class="eyebrow">LOG STUDY TIME</span>
        <h3>记录一次学习</h3>
      </div>
      <span class="study-form-icon" aria-hidden="true">＋</span>
    </div>

    <form class="study-record-form" @submit.prevent="submit">
      <label class="field study-task-field">
        <span>关联任务 <b>*</b></span>
        <select v-model="form.taskId" required @change="validationMessage = ''">
          <option value="" disabled>请选择学习任务</option>
          <option v-for="task in tasks" :key="task.id" :value="task.id">
            {{ task.title }}
          </option>
        </select>
      </label>

      <label class="field">
        <span>学习日期 <b>*</b></span>
        <input v-model="form.studyDate" type="date" required />
      </label>

      <label class="field">
        <span>学习时长（分钟）<b>*</b></span>
        <input
          v-model="form.durationMinutes"
          type="number"
          min="1"
          max="1440"
          step="1"
          required
          placeholder="例如：90"
          @input="validationMessage = ''"
        />
      </label>

      <label class="field study-note-field">
        <span>学习备注</span>
        <input v-model="form.note" type="text" maxlength="500" placeholder="今天完成了什么？" />
      </label>

      <p v-if="validationMessage" class="study-form-error" role="alert">{{ validationMessage }}</p>

      <button class="primary-button study-submit" type="submit" :disabled="submitting || tasks.length === 0">
        {{ submitting ? '正在记录…' : tasks.length === 0 ? '请先创建任务' : '保存学习记录' }}
      </button>
    </form>
  </section>
</template>
