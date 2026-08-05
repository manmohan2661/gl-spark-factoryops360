import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { SupplierPerformanceResponse, SupplierResponse } from '@/types/supplier';

export const supplierApi = {
  getSuppliers: async () => {
    const { data } = await apiClient.get<ApiResponse<SupplierResponse[]>>('/api/v1/suppliers');
    return data.data;
  },
  getSupplierById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<SupplierResponse>>(`/api/v1/suppliers/${id}`);
    return data.data;
  },
  getSupplierPerformances: async () => {
    const { data } = await apiClient.get<ApiResponse<SupplierPerformanceResponse[]>>('/api/v1/supplier-performances');
    return data.data;
  },
  getSupplierPerformanceById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<SupplierPerformanceResponse>>(`/api/v1/supplier-performances/${id}`);
    return data.data;
  },
};