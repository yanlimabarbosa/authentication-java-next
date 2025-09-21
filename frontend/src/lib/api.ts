import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios'

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'

const api: AxiosInstance = axios.create({
  baseURL,
  withCredentials: true,
})

let isRefreshing = false
let pendingQueue: Array<{ resolve: () => void; reject: (err: unknown) => void }> = []

function processQueue(error: unknown | null) {
  pendingQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error)
    else resolve()
  })
  pendingQueue = []
}

function getCsrfToken(): string | null {
  if (typeof document === 'undefined') return null
  const match = document.cookie.match(/csrf_access_token=([^;]+)/)
  return match ? match[1] : null
}

api.interceptors.request.use((config) => {
  const csrfToken = getCsrfToken()
  if (csrfToken) {
    config.headers['X-CSRF-Token'] = csrfToken
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const original = error.config as AxiosRequestConfig & { _retry?: boolean }
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true

      if (isRefreshing) {
        await new Promise<void>((resolve, reject) => pendingQueue.push({ resolve, reject }))
        return api(original)
      }

      isRefreshing = true
      try {
        await api.post('/api/auth/refresh')
        processQueue(null)
        return api(original)
      } catch (refreshErr) {
        processQueue(refreshErr)
        return Promise.reject(refreshErr)
      } finally {
        isRefreshing = false
      }
    }
    return Promise.reject(error)
  }
)

export default api


