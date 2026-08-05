import axios, { AxiosError } from 'axios';
import { storage } from '@/utils/storage';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
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
  (error: AxiosError<{ message?: string }>) => {
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;
      
      if (status === 401) {
        storage.clearToken();
        window.dispatchEvent(new Event('factoryops360:session-expired'));
      } else if ([400, 403, 404, 422, 500].includes(status)) {
        let title = 'Error';
        let description = data?.message || 'An unexpected error occurred';
        let variant: 'destructive' | 'warning' = 'destructive';

        switch (status) {
          case 400:
            title = 'Validation Error';
            variant = 'warning';
            break;
          case 403:
            title = 'Permission Denied';
            description = 'You do not have permission to perform this action';
            break;
          case 404:
            title = 'Resource Not Found';
            break;
          case 422:
            title = 'Invalid Data';
            variant = 'warning';
            break;
          case 500:
            title = 'Server Error';
            break;
        }

        window.dispatchEvent(
          new CustomEvent('factoryops360:api-error', {
            detail: { title, description, variant },
          })
        );
      }
    }

    return Promise.reject(error);
  },
);