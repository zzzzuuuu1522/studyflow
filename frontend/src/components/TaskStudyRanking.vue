<script setup>
defineProps({
  items: { type: Array, required: true },
  formatDuration: { type: Function, required: true },
})

function progress(item, items) {
  const max = items[0]?.durationMinutes || 1
  return `${Math.max((item.durationMinutes / max) * 100, 5)}%`
}
</script>

<template>
  <article class="insight-card ranking-card">
    <div class="insight-heading">
      <div>
        <span class="eyebrow">TOP TASKS</span>
        <h3>任务学习排行</h3>
      </div>
    </div>

    <div v-if="items.length === 0" class="study-empty compact-empty">
      <span>⌁</span>
      <p>记录学习后，这里会出现排行</p>
    </div>

    <ol v-else class="ranking-list">
      <li v-for="(item, index) in items" :key="item.taskId">
        <span class="rank-number">{{ String(index + 1).padStart(2, '0') }}</span>
        <div class="rank-content">
          <div class="rank-title">
            <strong>{{ item.taskTitle }}</strong>
            <span>{{ formatDuration(item.durationMinutes) }}</span>
          </div>
          <div class="rank-track">
            <div :style="{ width: progress(item, items) }"></div>
          </div>
        </div>
      </li>
    </ol>
  </article>
</template>
