import { apiClient } from '@/api/axios';
import type { ApiResponse, LoginRequest, LoginResponse } from '@/types/auth';

export const authApi = {
  login: async (payload: LoginRequest) => {
    const { data } = await apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', payload);
    return data.data;
  },
};