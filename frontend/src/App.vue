<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import TaskEditModal from './components/TaskEditModal.vue'
import TaskForm from './components/TaskForm.vue'
import TaskList from './components/TaskList.vue'
import StudyDashboard from './components/StudyDashboard.vue'
import {
  createTask,
  deleteTask,
  getTasks,
  updateTask,
  updateTaskStatus,
} from './services/taskApi'

const filters = [
  { value: '', label: '全部' },
  { value: 'NOT_STARTED', label: '未开始' },
  { value: 'IN_PROGRESS', label: '进行中' },
  { value: 'COMPLETED', label: '已完成' },
]

const priorityFilters = [
  { value: '', label: '全部优先级' },
  { value: 'HIGH', label: '高优先级' },
  { value: 'MEDIUM', label: '中优先级' },
  { value: 'LOW', label: '低优先级' },
]

const tasks = ref([])
const activeFilter = ref('')
const activePriority = ref('')
const loading = ref(true)
const creating = ref(false)
const saving = ref(false)
const statusUpdatingId = ref(null)
const deletingId = ref(null)
const editingTask = ref(null)
const errorMessage = ref('')
const toast = ref(null)
const taskForm = ref(null)
const studyRefreshKey = ref(0)
let toastTimer

const taskCountLabel = computed(() => `${tasks.value.length} 项任务`)

async function loadTasks() {
  loading.value = true
  errorMessage.value = ''
  try {
    tasks.value = await getTasks(activeFilter.value, activePriority.value)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

function showToast(message) {
  toast.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => (toast.value = null), 3000)
}

async function handleCreate(payload) {
  creating.value = true
  errorMessage.value = ''
  try {
    await createTask(payload)
    taskForm.value?.reset()
    showToast('任务创建成功')
    await loadTasks()
    studyRefreshKey.value++
  } catch (error) {
    errorMessage.value = `创建失败：${error.message}`
  } finally {
    creating.value = false
  }
}

async function handleSave(payload) {
  saving.value = true
  errorMessage.value = ''
  try {
    await updateTask(editingTask.value.id, payload)
    editingTask.value = null
    showToast('任务修改已保存')
    await loadTasks()
    studyRefreshKey.value++
  } catch (error) {
    errorMessage.value = `保存失败：${error.message}`
  } finally {
    saving.value = false
  }
}

async function handleStatusChange(task, status) {
  if (status === task.status) return
  statusUpdatingId.value = task.id
  errorMessage.value = ''
  try {
    await updateTaskStatus(task.id, status)
    showToast('任务状态已更新')
    await loadTasks()
  } catch (error) {
    errorMessage.value = `状态更新失败：${error.message}`
  } finally {
    statusUpdatingId.value = null
  }
}

async function handleDelete(task) {
  if (!window.confirm(`确定要删除“${task.title}”吗？该任务的学习记录也会被删除，此操作无法撤销。`)) return

  deletingId.value = task.id
  errorMessage.value = ''
  try {
    await deleteTask(task.id)
    showToast('任务已删除')
    await loadTasks()
    studyRefreshKey.value++
  } catch (error) {
    errorMessage.value = `删除失败：${error.message}`
  } finally {
    deletingId.value = null
  }
}

function changeFilter(status) {
  if (activeFilter.value === status) return
  activeFilter.value = status
  loadTasks()
}

function changePriority(priority) {
  if (activePriority.value === priority) return
  activePriority.value = priority
  loadTasks()
}

onMounted(loadTasks)
onBeforeUnmount(() => window.clearTimeout(toastTimer))
</script>

<template>
  <div class="app-shell">
    <header class="hero">
      <nav class="nav-bar" aria-label="顶部导航">
        <a class="brand" href="#">
          <span class="brand-mark">学</span>
          <span>StudyFlow</span>
        </a>
        <span class="connection-state"><i></i> 本地学习空间</span>
      </nav>

      <div class="hero-content">
        <div>
          <span class="hero-kicker">LEARN · PLAN · ACHIEVE</span>
          <h1>学习任务管理系统</h1>
          <p>管理你的学习计划与截止日期，让每一次投入都有清晰的方向。</p>
        </div>
        <div class="hero-shape" aria-hidden="true">
          <span>01</span>
          <small>保持专注<br />持续前进</small>
        </div>
      </div>
    </header>

    <main class="main-content">
      <TaskForm ref="taskForm" :submitting="creating" @submit="handleCreate" />

      <section class="tasks-section" aria-labelledby="tasks-title">
        <div class="tasks-toolbar">
          <div>
            <span class="eyebrow">YOUR PLAN</span>
            <div class="title-with-count">
              <h2 id="tasks-title">学习清单</h2>
              <span>{{ taskCountLabel }}</span>
            </div>
          </div>

          <div class="filters" aria-label="按状态筛选">
            <button
              v-for="filter in filters"
              :key="filter.value"
              type="button"
              :class="{ active: activeFilter === filter.value }"
              @click="changeFilter(filter.value)"
            >
              {{ filter.label }}
            </button>
          </div>

          <div class="filters priority-filters" aria-label="按优先级筛选">
            <button
              v-for="filter in priorityFilters"
              :key="filter.value"
              type="button"
              :class="{ active: activePriority === filter.value }"
              @click="changePriority(filter.value)"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>

        <div v-if="errorMessage" class="error-banner" role="alert">
          <span aria-hidden="true">!</span>
          <p>{{ errorMessage }}</p>
          <button type="button" @click="loadTasks">重试</button>
        </div>

        <TaskList
          :tasks="tasks"
          :loading="loading"
          :status-updating-id="statusUpdatingId"
          :deleting-id="deletingId"
          @edit="editingTask = $event"
          @delete="handleDelete"
          @status-change="handleStatusChange"
        />
      </section>

      <StudyDashboard :refresh-key="studyRefreshKey" />
    </main>

    <footer>
      <span>StudyFlow</span>
      <p>今天完成的一小步，会成为明天扎实的底气。</p>
    </footer>

    <Transition name="toast">
      <div v-if="toast" class="toast" role="status">
        <span>✓</span>{{ toast }}
      </div>
    </Transition>

    <TaskEditModal
      v-if="editingTask"
      :task="editingTask"
      :saving="saving"
      @close="editingTask = null"
      @save="handleSave"
    />
  </div>
</template>
