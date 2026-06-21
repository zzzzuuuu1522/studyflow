const STUDY_RECORDS_URL = 'http://localhost:8080/api/study-records'
const STATISTICS_URL = 'http://localhost:8080/api/statistics/dashboard'

async function request(url, options = {}) {
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...options.headers,
      },
    })

    if (response.status === 204) return null

    const data = await response.json().catch(() => null)
    if (!response.ok) {
      throw new Error(data?.message || `请求失败（HTTP ${response.status}）`)
    }
    return data
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('无法连接后端服务，请确认 Spring Boot 已在 8080 端口启动')
    }
    throw error
  }
}

function withQuery(baseUrl, params) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) query.set(key, value)
  })
  return query.size ? `${baseUrl}?${query}` : baseUrl
}

export function getStudyRecords(params = {}) {
  return request(withQuery(STUDY_RECORDS_URL, params))
}

export function createStudyRecord(record) {
  return request(STUDY_RECORDS_URL, {
    method: 'POST',
    body: JSON.stringify(record),
  })
}

export function updateStudyRecord(id, record) {
  return request(`${STUDY_RECORDS_URL}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(record),
  })
}

export function deleteStudyRecord(id) {
  return request(`${STUDY_RECORDS_URL}/${id}`, { method: 'DELETE' })
}

export function getDashboardStatistics(params = {}) {
  return request(withQuery(STATISTICS_URL, params))
}
