import axios, { AxiosError } from 'axios';
import { storage } from '@/utils/storage';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = storage.getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      storage.clearToken();
      window.dispatchEvent(new Event('factoryops360:session-expired'));
    }

    return Promise.reject(error);
  },
);