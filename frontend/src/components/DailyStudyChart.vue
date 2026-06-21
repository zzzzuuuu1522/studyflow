<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, required: true },
})

const maxMinutes = computed(() => Math.max(...props.items.map((item) => item.durationMinutes), 1))

function barHeight(minutes) {
  return `${Math.max((minutes / maxMinutes.value) * 100, 8)}%`
}

function shortDate(date) {
  return date.slice(5).replace('-', '/')
}
</script>

<template>
  <article class="insight-card chart-card">
    <div class="insight-heading">
      <div>
        <span class="eyebrow">DAILY FOCUS</span>
        <h3>每日学习时长</h3>
      </div>
      <span class="chart-unit">分钟</span>
    </div>

    <div v-if="items.length === 0" class="study-empty compact-empty">
      <span>▥</span>
      <p>当前范围还没有学习数据</p>
    </div>

    <div v-else class="bar-chart" role="img" aria-label="每日学习时长柱状图">
      <div
        v-for="item in items"
        :key="item.date"
        class="bar-column"
        :title="`${item.date}：${item.durationMinutes} 分钟`"
      >
        <span class="bar-value">{{ item.durationMinutes }}</span>
        <div class="bar-track">
          <div class="bar-fill" :style="{ height: barHeight(item.durationMinutes) }"></div>
        </div>
        <span class="bar-label">{{ shortDate(item.date) }}</span>
      </div>
    </div>
  </article>
</template>
