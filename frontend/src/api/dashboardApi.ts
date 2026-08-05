import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { DashboardResponse } from '@/types/dashboard';

export const dashboardApi = {
  getDashboard: async () => {
    const { data } = await apiClient.get<ApiResponse<DashboardResponse>>('/api/v1/dashboard');
    return data.data;
  },
};