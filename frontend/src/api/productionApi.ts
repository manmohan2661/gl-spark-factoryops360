import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type {
  MachineMaintenanceResponse,
  MachineResponse,
  ProductionBatchResponse,
  ProductionOrderResponse,
  ShiftResponse,
} from '@/types/production';

export const productionApi = {
  getShifts: async () => {
    const { data } = await apiClient.get<ApiResponse<ShiftResponse[]>>('/api/v1/shifts');
    return data.data;
  },
  getShiftById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<ShiftResponse>>(`/api/v1/shifts/${id}`);
    return data.data;
  },
  getProductionOrders: async () => {
    const { data } = await apiClient.get<ApiResponse<ProductionOrderResponse[]>>('/api/v1/production-orders');
    return data.data;
  },
  getProductionOrderById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<ProductionOrderResponse>>(`/api/v1/production-orders/${id}`);
    return data.data;
  },
  getProductionBatches: async () => {
    const { data } = await apiClient.get<ApiResponse<ProductionBatchResponse[]>>('/api/v1/production-batches');
    return data.data;
  },
  getProductionBatchById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<ProductionBatchResponse>>(`/api/v1/production-batches/${id}`);
    return data.data;
  },
  getMachines: async () => {
    const { data } = await apiClient.get<ApiResponse<MachineResponse[]>>('/api/v1/machines');
    return data.data;
  },
  getMachineById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<MachineResponse>>(`/api/v1/machines/${id}`);
    return data.data;
  },
  getMachineMaintenances: async () => {
    const { data } = await apiClient.get<ApiResponse<MachineMaintenanceResponse[]>>('/api/v1/machine-maintenances');
    return data.data;
  },
  getMachineMaintenanceById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<MachineMaintenanceResponse>>(`/api/v1/machine-maintenances/${id}`);
    return data.data;
  },
};