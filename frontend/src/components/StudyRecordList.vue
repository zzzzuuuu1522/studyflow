<script setup>
defineProps({
  records: { type: Array, required: true },
  loading: { type: Boolean, default: false },
  deletingId: { type: Number, default: null },
  formatDuration: { type: Function, required: true },
})

const emit = defineEmits(['edit', 'delete'])

function formatDate(date) {
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'short',
  }).format(new Date(`${date}T00:00:00`))
}
</script>

<template>
  <section class="records-panel">
    <div class="records-heading">
      <div>
        <span class="eyebrow">STUDY HISTORY</span>
        <h3>学习记录</h3>
      </div>
      <span>{{ records.length }} 条</span>
    </div>

    <div v-if="loading" class="study-empty">
      <span class="loader"></span>
      <p>正在加载学习记录…</p>
    </div>

    <div v-else-if="records.length === 0" class="study-empty">
      <span class="record-empty-icon">◷</span>
      <h4>还没有学习记录</h4>
      <p>完成一次专注学习后，把它记录下来吧</p>
    </div>

    <div v-else class="record-list">
      <article v-for="record in records" :key="record.id" class="record-row">
        <div class="record-date">
          <strong>{{ formatDate(record.studyDate) }}</strong>
          <span>{{ record.taskTitle }}</span>
        </div>
        <div class="record-note">{{ record.note || '这次学习没有填写备注。' }}</div>
        <strong class="record-duration">{{ formatDuration(record.durationMinutes) }}</strong>
        <div class="record-actions">
          <button type="button" @click="emit('edit', record)">编辑</button>
          <button
            class="danger"
            type="button"
            :disabled="deletingId === record.id"
            @click="emit('delete', record)"
          >
            {{ deletingId === record.id ? '删除中…' : '删除' }}
          </button>
        </div>
      </article>
    </div>
  </section>
</template>
