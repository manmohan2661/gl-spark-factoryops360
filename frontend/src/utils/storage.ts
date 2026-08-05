export const storage = {
  getToken: () => sessionStorage.getItem('factoryops360.token'),
  setToken: (token: string) => sessionStorage.setItem('factoryops360.token', token),
  clearToken: () => sessionStorage.removeItem('factoryops360.token'),
  getTheme: () => localStorage.getItem('factoryops360.theme'),
  setTheme: (theme: 'light' | 'dark') => localStorage.setItem('factoryops360.theme', theme),
};