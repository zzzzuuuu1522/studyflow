<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { getTasks } from '../services/taskApi'
import {
  createStudyRecord,
  deleteStudyRecord,
  getDashboardStatistics,
  getStudyRecords,
  updateStudyRecord,
} from '../services/studyApi'
import DailyStudyChart from './DailyStudyChart.vue'
import StudyRecordEditModal from './StudyRecordEditModal.vue'
import StudyRecordForm from './StudyRecordForm.vue'
import StudyRecordList from './StudyRecordList.vue'
import TaskStudyRanking from './TaskStudyRanking.vue'

const props = defineProps({
  refreshKey: { type: Number, default: 0 },
})

const ranges = [
  { value: 'LAST_7_DAYS', label: '最近 7 天' },
  { value: 'THIS_MONTH', label: '本月' },
  { value: 'ALL', label: '全部记录' },
]

const emptyStatistics = () => ({
  startDate: '',
  endDate: '',
  todayMinutes: 0,
  currentWeekMinutes: 0,
  currentMonthMinutes: 0,
  periodTotalMinutes: 0,
  recordCount: 0,
  dailyTotals: [],
  taskTotals: [],
})

const tasks = ref([])
const records = ref([])
const statistics = ref(emptyStatistics())
const activeRange = ref('LAST_7_DAYS')
const loading = ref(true)
const submitting = ref(false)
const saving = ref(false)
const deletingId = ref(null)
const editingRecord = ref(null)
const errorMessage = ref('')
const toast = ref('')
const recordForm = ref(null)
let toastTimer

const rangeDescription = computed(() => {
  if (!statistics.value.startDate || !statistics.value.endDate) return ''
  return `${statistics.value.startDate} 至 ${statistics.value.endDate}`
})

function localDateString(date = new Date()) {
  const offset = date.getTimezoneOffset() * 60_000
  return new Date(date.getTime() - offset).toISOString().slice(0, 10)
}

function addDays(dateString, days) {
  const date = new Date(`${dateString}T00:00:00`)
  date.setDate(date.getDate() + days)
  return localDateString(date)
}

function standardRangeParams() {
  const today = localDateString()
  if (activeRange.value === 'LAST_7_DAYS') {
    return { startDate: addDays(today, -6), endDate: today }
  }
  if (activeRange.value === 'THIS_MONTH') {
    return { startDate: `${today.slice(0, 7)}-01`, endDate: today }
  }
  return null
}

async function loadStudyData() {
  loading.value = true
  errorMessage.value = ''
  try {
    const params = standardRangeParams()
    if (params) {
      const [recordData, statisticData] = await Promise.all([
        getStudyRecords(params),
        getDashboardStatistics(params),
      ])
      records.value = recordData
      statistics.value = statisticData
    } else {
      // “全部记录”先读取完整列表，再用真实首尾日期请求统计。
      const recordData = await getStudyRecords()
      records.value = recordData
      const today = localDateString()
      const dates = recordData.map((record) => record.studyDate).sort()
      const allParams = {
        startDate: dates[0] || today,
        endDate: dates.length ? [today, dates.at(-1)].sort().at(-1) : today,
      }
      statistics.value = await getDashboardStatistics(allParams)
    }
  } catch (error) {
    errorMessage.value = error.message
    records.value = []
    statistics.value = emptyStatistics()
  } finally {
    loading.value = false
  }
}

async function loadEverything() {
  try {
    tasks.value = await getTasks()
  } catch (error) {
    errorMessage.value = `任务加载失败：${error.message}`
  }
  await loadStudyData()
}

function showToast(message) {
  toast.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => (toast.value = ''), 3000)
}

async function handleCreate(payload) {
  submitting.value = true
  errorMessage.value = ''
  try {
    await createStudyRecord(payload)
    recordForm.value?.reset()
    showToast('学习记录创建成功')
    await loadStudyData()
  } catch (error) {
    errorMessage.value = `创建学习记录失败：${error.message}`
  } finally {
    submitting.value = false
  }
}

async function handleSave(payload) {
  saving.value = true
  errorMessage.value = ''
  try {
    await updateStudyRecord(editingRecord.value.id, payload)
    editingRecord.value = null
    showToast('学习记录修改成功')
    await loadStudyData()
  } catch (error) {
    errorMessage.value = `修改学习记录失败：${error.message}`
  } finally {
    saving.value = false
  }
}

async function handleDelete(record) {
  if (!window.confirm(`确定删除 ${record.studyDate} 的这条学习记录吗？`)) return
  deletingId.value = record.id
  errorMessage.value = ''
  try {
    await deleteStudyRecord(record.id)
    showToast('学习记录已删除')
    await loadStudyData()
  } catch (error) {
    errorMessage.value = `删除学习记录失败：${error.message}`
  } finally {
    deletingId.value = null
  }
}

function changeRange(range) {
  if (activeRange.value === range) return
  activeRange.value = range
  loadStudyData()
}

function formatDuration(minutes = 0) {
  const hours = Math.floor(minutes / 60)
  const restMinutes = minutes % 60
  if (hours === 0) return `${restMinutes} 分钟`
  if (restMinutes === 0) return `${hours} 小时`
  return `${hours} 小时 ${restMinutes} 分钟`
}

onMounted(loadEverything)
watch(() => props.refreshKey, loadEverything)
onBeforeUnmount(() => window.clearTimeout(toastTimer))
</script>

<template>
  <section class="study-dashboard" aria-labelledby="study-dashboard-title">
    <div class="study-section-header">
      <div>
        <span class="eyebrow">LEARNING ANALYTICS</span>
        <h2 id="study-dashboard-title">学习时长统计</h2>
        <p>记录每一次专注，看见持续学习带来的积累。</p>
      </div>
      <div class="range-summary">
        <span>当前统计范围</span>
        <strong>{{ rangeDescription || '正在计算…' }}</strong>
      </div>
    </div>

    <div class="study-range-tabs" aria-label="统计范围">
      <button
        v-for="range in ranges"
        :key="range.value"
        type="button"
        :class="{ active: activeRange === range.value }"
        @click="changeRange(range.value)"
      >
        {{ range.label }}
      </button>
    </div>

    <div v-if="errorMessage" class="error-banner study-error" role="alert">
      <span aria-hidden="true">!</span>
      <p>{{ errorMessage }}</p>
      <button type="button" @click="loadEverything">重试</button>
    </div>

    <div class="statistics-grid" :class="{ 'is-loading': loading }">
      <article>
        <span>今天学习</span>
        <strong>{{ formatDuration(statistics.todayMinutes) }}</strong>
        <small>TODAY</small>
      </article>
      <article>
        <span>本周学习</span>
        <strong>{{ formatDuration(statistics.currentWeekMinutes) }}</strong>
        <small>THIS WEEK</small>
      </article>
      <article>
        <span>本月学习</span>
        <strong>{{ formatDuration(statistics.currentMonthMinutes) }}</strong>
        <small>THIS MONTH</small>
      </article>
      <article class="highlight-stat">
        <span>当前范围总计</span>
        <strong>{{ formatDuration(statistics.periodTotalMinutes) }}</strong>
        <small>SELECTED RANGE</small>
      </article>
      <article>
        <span>学习记录</span>
        <strong>{{ statistics.recordCount }} 条</strong>
        <small>RECORDS</small>
      </article>
    </div>

    <div class="insights-grid">
      <DailyStudyChart :items="statistics.dailyTotals" />
      <TaskStudyRanking :items="statistics.taskTotals" :format-duration="formatDuration" />
    </div>

    <StudyRecordForm
      ref="recordForm"
      :tasks="tasks"
      :submitting="submitting"
      @submit="handleCreate"
    />

    <StudyRecordList
      :records="records"
      :loading="loading"
      :deleting-id="deletingId"
      :format-duration="formatDuration"
      @edit="editingRecord = $event"
      @delete="handleDelete"
    />

    <Transition name="toast">
      <div v-if="toast" class="toast" role="status"><span>✓</span>{{ toast }}</div>
    </Transition>

    <StudyRecordEditModal
      v-if="editingRecord"
      :record="editingRecord"
      :tasks="tasks"
      :saving="saving"
      @close="editingRecord = null"
      @save="handleSave"
    />
  </section>
</template>
