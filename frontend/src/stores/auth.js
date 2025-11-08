import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    user: null,
    role: null,
    isAuthenticated: false,
  }),
  actions: {
    initAuth() {
      const savedToken = localStorage.getItem('token') || ''
      const savedUser = localStorage.getItem('user')
      const savedRole = localStorage.getItem('role')
      this.token = savedToken
      this.user = savedUser ? JSON.parse(savedUser) : null
      this.role = savedRole
      this.isAuthenticated = !!savedToken
    },
    setToken(token) {
      this.token = token
      this.isAuthenticated = !!token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    setUser(user) {
      this.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    setRole(role) {
      this.role = role
      if (role !== undefined && role !== null) {
        localStorage.setItem('role', role)
      } else {
        localStorage.removeItem('role')
      }
    },
    logout() {
      this.setToken('')
      this.setUser(null)
      this.setRole(null)
      this.isAuthenticated = false
    }
  },
  getters: {
    userRole: (state) => state.role,
  }
})


