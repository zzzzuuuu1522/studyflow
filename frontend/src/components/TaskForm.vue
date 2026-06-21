<script setup>
import { reactive } from 'vue'

defineProps({
  submitting: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['submit'])

const initialForm = () => ({
  title: '',
  description: '',
  status: 'NOT_STARTED',
  priority: 'MEDIUM',
  deadline: '',
})

const form = reactive(initialForm())

function submitForm() {
  emit('submit', {
    ...form,
    title: form.title.trim(),
    description: form.description.trim() || null,
    deadline: form.deadline || null,
  })
}

function reset() {
  Object.assign(form, initialForm())
}

defineExpose({ reset })
</script>

<template>
  <section class="create-panel" aria-labelledby="create-title">
    <div class="section-heading">
      <div>
        <span class="eyebrow">NEW TASK</span>
        <h2 id="create-title">添加学习任务</h2>
      </div>
      <span class="heading-icon" aria-hidden="true">＋</span>
    </div>

    <form class="task-form" @submit.prevent="submitForm">
      <label class="field field-wide">
        <span>任务标题 <b>*</b></span>
        <input
          v-model="form.title"
          type="text"
          maxlength="100"
          required
          placeholder="例如：完成 Spring Boot 接口练习"
        />
      </label>

      <label class="field field-wide">
        <span>任务描述</span>
        <textarea
          v-model="form.description"
          maxlength="500"
          rows="3"
          placeholder="写下目标、资料或需要注意的内容…"
        ></textarea>
      </label>

      <label class="field">
        <span>优先级</span>
        <select v-model="form.priority">
          <option value="HIGH">高优先级</option>
          <option value="MEDIUM">中优先级</option>
          <option value="LOW">低优先级</option>
        </select>
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

      <button class="primary-button field-wide" type="submit" :disabled="submitting">
        <span>{{ submitting ? '正在创建…' : '创建任务' }}</span>
        <span aria-hidden="true">→</span>
      </button>
    </form>
  </section>
</template>
