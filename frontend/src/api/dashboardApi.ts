import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { DashboardResponse, AuditLogResponse } from '@/types/dashboard';
import type { RecommendationResponse } from '@/types/recommendation';

export const dashboardApi = {
  getDashboard: async () => {
    const { data } = await apiClient.get<ApiResponse<DashboardResponse>>('/api/v1/dashboard');
    return data.data;
  },
  getAuditLogs: async () => {
    const { data } = await apiClient.get<ApiResponse<AuditLogResponse[]>>('/api/v1/audit-logs');
    return data.data;
  },
  getRecommendations: async () => {
    const { data } = await apiClient.get<ApiResponse<RecommendationResponse[]>>('/api/v1/recommendations');
    return data.data;
  },
};