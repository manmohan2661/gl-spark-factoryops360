import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type {
  MachineMaintenanceResponse,
  MachineResponse,
  ProductionBatchResponse,
  ProductionOrderResponse,
  ShiftResponse,
  ProductionOrderRequest,
  MachineRequest,
  MachineMaintenanceRequest,
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
  createProductionOrder: async (payload: ProductionOrderRequest) => {
    const { data } = await apiClient.post<ApiResponse<ProductionOrderResponse>>('/api/v1/production-orders', payload);
    return data.data;
  },
  updateProductionOrder: async (id: number, payload: ProductionOrderRequest) => {
    const { data } = await apiClient.put<ApiResponse<ProductionOrderResponse>>(`/api/v1/production-orders/${id}`, payload);
    return data.data;
  },
  deleteProductionOrder: async (id: number) => {
    await apiClient.delete(`/api/v1/production-orders/${id}`);
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
  createMachine: async (payload: MachineRequest) => {
    const { data } = await apiClient.post<ApiResponse<MachineResponse>>('/api/v1/machines', payload);
    return data.data;
  },
  updateMachine: async (id: number, payload: MachineRequest) => {
    const { data } = await apiClient.put<ApiResponse<MachineResponse>>(`/api/v1/machines/${id}`, payload);
    return data.data;
  },
  deleteMachine: async (id: number) => {
    await apiClient.delete(`/api/v1/machines/${id}`);
  },
  getMachineMaintenances: async () => {
    const { data } = await apiClient.get<ApiResponse<MachineMaintenanceResponse[]>>('/api/v1/machine-maintenances');
    return data.data;
  },
  getMachineMaintenanceById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<MachineMaintenanceResponse>>(`/api/v1/machine-maintenances/${id}`);
    return data.data;
  },
  createMachineMaintenance: async (payload: MachineMaintenanceRequest) => {
    const { data } = await apiClient.post<ApiResponse<MachineMaintenanceResponse>>('/api/v1/machine-maintenances', payload);
    return data.data;
  },
  updateMachineMaintenance: async (id: number, payload: MachineMaintenanceRequest) => {
    const { data } = await apiClient.put<ApiResponse<MachineMaintenanceResponse>>(`/api/v1/machine-maintenances/${id}`, payload);
    return data.data;
  },
  deleteMachineMaintenance: async (id: number) => {
    await apiClient.delete(`/api/v1/machine-maintenances/${id}`);
  },
};