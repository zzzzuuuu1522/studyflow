<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'

const props = defineProps({
  task: {
    type: Object,
    required: true,
  },
  saving: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['close', 'save'])
const validationMessage = ref('')

const form = reactive({
  title: props.task.title,
  description: props.task.description || '',
  status: props.task.status,
  priority: props.task.priority || 'MEDIUM',
  deadline: props.task.deadline || '',
})

function save() {
  if (!form.priority) {
    validationMessage.value = '请选择优先级'
    return
  }

  validationMessage.value = ''
  emit('save', {
    title: form.title.trim(),
    description: form.description.trim() || null,
    status: form.status,
    priority: form.priority,
    deadline: form.deadline || null,
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
    <section class="modal" role="dialog" aria-modal="true" aria-labelledby="edit-title">
      <div class="modal-header">
        <div>
          <span class="eyebrow">EDIT TASK</span>
          <h2 id="edit-title">编辑学习任务</h2>
        </div>
        <button class="close-button" type="button" aria-label="关闭" :disabled="saving" @click="emit('close')">×</button>
      </div>

      <form class="task-form" @submit.prevent="save">
        <label class="field field-wide">
          <span>任务标题 <b>*</b></span>
          <input v-model="form.title" type="text" maxlength="100" required autofocus />
        </label>

        <label class="field field-wide">
          <span>任务描述</span>
          <textarea v-model="form.description" maxlength="500" rows="4"></textarea>
        </label>

        <label class="field">
          <span>优先级</span>
          <select v-model="form.priority" required @change="validationMessage = ''">
            <option value="HIGH">高优先级</option>
            <option value="MEDIUM">中优先级</option>
            <option value="LOW">低优先级</option>
          </select>
          <small v-if="validationMessage" class="field-error" role="alert">
            {{ validationMessage }}
          </small>
        </label>

        <label class="field">
          <span>当前状态</span>
          <select v-model="form.status">
            <option value="NOT_STARTED">未开始</option>
            <option value="IN_PROGRESS">进行中</option>
            <option value="COMPLETED">已完成</option>
          </select>
        </label>

        <label class="field">
          <span>截止日期</span>
          <input v-model="form.deadline" type="date" />
        </label>

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
