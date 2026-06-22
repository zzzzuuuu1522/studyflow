import { API_BASE_URL, createApiError, createConnectionError } from './apiConfig'

const TASKS_URL = `${API_BASE_URL}/api/tasks`

// 集中处理 HTTP 响应，让所有组件获得一致、易懂的错误信息。
async function request(url, options = {}) {
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...options.headers,
      },
    })

    if (response.status === 204) {
      return null
    }

    const data = await response.json().catch(() => null)

    if (!response.ok) {
      throw createApiError(data, response.status)
    }

    return data
  } catch (error) {
    if (error instanceof TypeError) {
      throw createConnectionError()
    }
    throw error
  }
}

export function getTasks(status = '', priority = '') {
  const params = new URLSearchParams()
  if (status) params.set('status', status)
  if (priority) params.set('priority', priority)

  const query = params.toString()
  const url = query ? `${TASKS_URL}?${query}` : TASKS_URL
  return request(url)
}

export function createTask(task) {
  return request(TASKS_URL, {
    method: 'POST',
    body: JSON.stringify(task),
  })
}

export function updateTask(id, task) {
  if (!task.priority) {
    return Promise.reject(new Error('请选择优先级'))
  }

  // 明确构造 PUT 契约，避免表单字段在传递过程中被遗漏。
  const payload = {
    title: task.title,
    description: task.description,
    status: task.status,
    priority: task.priority,
    deadline: task.deadline,
  }

  return request(`${TASKS_URL}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function updateTaskStatus(id, status) {
  return request(`${TASKS_URL}/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  })
}

export function deleteTask(id) {
  return request(`${TASKS_URL}/${id}`, { method: 'DELETE' })
}
