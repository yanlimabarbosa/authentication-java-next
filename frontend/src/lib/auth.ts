import api from './api'

export type LoginResponse = { access_token?: string; refresh_token?: string }

export async function signup(email: string, name: string, password: string) {
  await api.post('/api/auth/signup', {
    email,
    name,
    password,
  })
}

export async function login(email: string, password: string, httponly = true) {
  const res = await api.post<LoginResponse>(`/api/auth/login?httponly=${httponly}`, {
    email,
    password,
  })
  return res.data
}

export async function logout() {
  await api.delete('/api/auth/revoke-tokens')
}

export async function getSelfPermissions() {
  const res = await api.get<Record<string, Record<string, boolean>>>('/api/admin/self/permissions')
  return res.data
}


