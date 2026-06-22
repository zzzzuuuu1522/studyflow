const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim()

export const API_BASE_URL = (configuredBaseUrl || 'http://localhost:8080').replace(/\/+$/, '')

const DEMO_READ_ONLY_MESSAGE = '当前为公开演示只读模式，数据仅供浏览，暂不支持新增、修改或删除。'

export function createApiError(data, status) {
  const code = data?.code || null
  const message = code === 'DEMO_READ_ONLY'
    ? DEMO_READ_ONLY_MESSAGE
    : data?.message || `请求失败（HTTP ${status}）`
  const error = new Error(message)
  error.code = code
  return error
}

export function createConnectionError() {
  return new Error('无法连接后端服务，请确认服务已启动或稍后重试')
}
