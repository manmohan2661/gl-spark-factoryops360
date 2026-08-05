import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { AlertResponse } from '@/types/alerts';

export const alertApi = {
  getAlerts: async () => {
    const { data } = await apiClient.get<ApiResponse<AlertResponse[]>>('/api/v1/alerts');
    return data.data;
  },
  getAlertById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<AlertResponse>>(`/api/v1/alerts/${id}`);
    return data.data;
  },
};
