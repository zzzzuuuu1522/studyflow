<script setup>
import TaskItem from './TaskItem.vue'

defineProps({
  tasks: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  statusUpdatingId: {
    type: Number,
    default: null,
  },
  deletingId: {
    type: Number,
    default: null,
  },
})

const emit = defineEmits(['edit', 'delete', 'status-change'])
</script>

<template>
  <div v-if="loading" class="state-card" aria-live="polite">
    <span class="loader"></span>
    <p>正在加载任务…</p>
  </div>

  <div v-else-if="tasks.length === 0" class="state-card empty-state">
    <div class="empty-icon" aria-hidden="true">✓</div>
    <h3>清单还是空的</h3>
    <p>暂时没有学习任务，先创建一个吧</p>
  </div>

  <div v-else class="task-list">
    <TaskItem
      v-for="task in tasks"
      :key="task.id"
      :task="task"
      :status-updating="statusUpdatingId === task.id"
      :deleting="deletingId === task.id"
      @edit="emit('edit', task)"
      @delete="emit('delete', task)"
      @status-change="emit('status-change', task, $event)"
    />
  </div>
</template>
