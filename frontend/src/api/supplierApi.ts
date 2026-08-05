import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { SupplierPerformanceResponse, SupplierResponse, SupplierRequest, SupplierPerformanceRequest } from '@/types/supplier';

export const supplierApi = {
  getSuppliers: async () => {
    const { data } = await apiClient.get<ApiResponse<SupplierResponse[]>>('/api/v1/suppliers');
    return data.data;
  },
  getSupplierById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<SupplierResponse>>(`/api/v1/suppliers/${id}`);
    return data.data;
  },
  createSupplier: async (payload: SupplierRequest) => {
    const { data } = await apiClient.post<ApiResponse<SupplierResponse>>('/api/v1/suppliers', payload);
    return data.data;
  },
  updateSupplier: async (id: number, payload: SupplierRequest) => {
    const { data } = await apiClient.put<ApiResponse<SupplierResponse>>(`/api/v1/suppliers/${id}`, payload);
    return data.data;
  },
  deleteSupplier: async (id: number) => {
    await apiClient.delete(`/api/v1/suppliers/${id}`);
  },
  getSupplierPerformances: async () => {
    const { data } = await apiClient.get<ApiResponse<SupplierPerformanceResponse[]>>('/api/v1/supplier-performances');
    return data.data;
  },
  getSupplierPerformanceById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<SupplierPerformanceResponse>>(`/api/v1/supplier-performances/${id}`);
    return data.data;
  },
  createSupplierPerformance: async (payload: SupplierPerformanceRequest) => {
    const { data } = await apiClient.post<ApiResponse<SupplierPerformanceResponse>>('/api/v1/supplier-performances', payload);
    return data.data;
  },
  updateSupplierPerformance: async (id: number, payload: SupplierPerformanceRequest) => {
    const { data } = await apiClient.put<ApiResponse<SupplierPerformanceResponse>>(`/api/v1/supplier-performances/${id}`, payload);
    return data.data;
  },
  deleteSupplierPerformance: async (id: number) => {
    await apiClient.delete(`/api/v1/supplier-performances/${id}`);
  },
};