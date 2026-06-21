<script setup>
const props = defineProps({
  task: {
    type: Object,
    required: true,
  },
  statusUpdating: {
    type: Boolean,
    default: false,
  },
  deleting: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['edit', 'delete', 'status-change'])

const statusMeta = {
  NOT_STARTED: { label: '未开始', className: 'not-started' },
  IN_PROGRESS: { label: '进行中', className: 'in-progress' },
  COMPLETED: { label: '已完成', className: 'completed' },
}

const priorityMeta = {
  HIGH: { label: '高优先级', className: 'priority-high' },
  MEDIUM: { label: '中优先级', className: 'priority-medium' },
  LOW: { label: '低优先级', className: 'priority-low' },
}

function formatDate(date) {
  if (!date) return '未设置'
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(new Date(`${date}T00:00:00`))
}

function formatDateTime(dateTime) {
  if (!dateTime) return '—'
  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(new Date(dateTime))
}

function changeStatus(event) {
  const nextStatus = event.target.value
  event.target.value = props.task.status
  emit('status-change', nextStatus)
}
</script>

<template>
  <article class="task-card" :class="{ 'task-completed': task.status === 'COMPLETED' }">
    <div class="task-main">
      <div class="task-title-row">
        <span class="status-dot" :class="statusMeta[task.status].className"></span>
        <div>
          <h3>{{ task.title }}</h3>
          <span class="status-badge" :class="statusMeta[task.status].className">
            {{ statusMeta[task.status].label }}
          </span>
          <span
            class="priority-badge"
            :class="priorityMeta[task.priority || 'MEDIUM'].className"
          >
            {{ priorityMeta[task.priority || 'MEDIUM'].label }}
          </span>
        </div>
      </div>

      <p class="task-description">
        {{ task.description || '这项任务暂时没有补充说明。' }}
      </p>

      <div class="task-meta">
        <span>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M7 3v3m10-3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Z" />
          </svg>
          截止 {{ formatDate(task.deadline) }}
        </span>
        <span>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="12" cy="12" r="9" />
            <path d="M12 7v5l3 2" />
          </svg>
          创建于 {{ formatDateTime(task.createdAt) }}
        </span>
      </div>
    </div>

    <div class="task-actions">
      <label class="quick-status">
        <span>快速更新</span>
        <select
          :value="task.status"
          :disabled="statusUpdating || deleting"
          @change="changeStatus"
        >
          <option value="NOT_STARTED">未开始</option>
          <option value="IN_PROGRESS">进行中</option>
          <option value="COMPLETED">已完成</option>
        </select>
      </label>

      <div class="action-buttons">
        <button class="text-button" type="button" :disabled="deleting" @click="emit('edit')">
          编辑
        </button>
        <button class="text-button danger" type="button" :disabled="deleting" @click="emit('delete')">
          {{ deleting ? '删除中…' : '删除' }}
        </button>
      </div>
    </div>
  </article>
</template>
