import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type {
  InventoryResponse,
  InventoryTransactionResponse,
  MaterialResponse,
  WarehouseResponse,
  InventoryRequest,
  MaterialRequest,
  WarehouseRequest,
} from '@/types/inventory';

export const inventoryApi = {
  getWarehouses: async () => {
    const { data } = await apiClient.get<ApiResponse<WarehouseResponse[]>>('/api/v1/warehouses');
    return data.data;
  },
  getWarehouseById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<WarehouseResponse>>(`/api/v1/warehouses/${id}`);
    return data.data;
  },
  createWarehouse: async (payload: WarehouseRequest) => {
    const { data } = await apiClient.post<ApiResponse<WarehouseResponse>>('/api/v1/warehouses', payload);
    return data.data;
  },
  updateWarehouse: async (id: number, payload: WarehouseRequest) => {
    const { data } = await apiClient.put<ApiResponse<WarehouseResponse>>(`/api/v1/warehouses/${id}`, payload);
    return data.data;
  },
  deleteWarehouse: async (id: number) => {
    await apiClient.delete(`/api/v1/warehouses/${id}`);
  },
  getMaterials: async () => {
    const { data } = await apiClient.get<ApiResponse<MaterialResponse[]>>('/api/v1/materials');
    return data.data;
  },
  getMaterialById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<MaterialResponse>>(`/api/v1/materials/${id}`);
    return data.data;
  },
  createMaterial: async (payload: MaterialRequest) => {
    const { data } = await apiClient.post<ApiResponse<MaterialResponse>>('/api/v1/materials', payload);
    return data.data;
  },
  updateMaterial: async (id: number, payload: MaterialRequest) => {
    const { data } = await apiClient.put<ApiResponse<MaterialResponse>>(`/api/v1/materials/${id}`, payload);
    return data.data;
  },
  deleteMaterial: async (id: number) => {
    await apiClient.delete(`/api/v1/materials/${id}`);
  },
  getInventories: async () => {
    const { data } = await apiClient.get<ApiResponse<InventoryResponse[]>>('/api/v1/inventories');
    return data.data;
  },
  getInventoryById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<InventoryResponse>>(`/api/v1/inventories/${id}`);
    return data.data;
  },
  createInventory: async (payload: InventoryRequest) => {
    const { data } = await apiClient.post<ApiResponse<InventoryResponse>>('/api/v1/inventories', payload);
    return data.data;
  },
  updateInventory: async (id: number, payload: InventoryRequest) => {
    const { data } = await apiClient.put<ApiResponse<InventoryResponse>>(`/api/v1/inventories/${id}`, payload);
    return data.data;
  },
  deleteInventory: async (id: number) => {
    await apiClient.delete(`/api/v1/inventories/${id}`);
  },
  getInventoryTransactions: async () => {
    const { data } = await apiClient.get<ApiResponse<InventoryTransactionResponse[]>>('/api/v1/inventory-transactions');
    return data.data;
  },
  getInventoryTransactionById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<InventoryTransactionResponse>>(`/api/v1/inventory-transactions/${id}`);
    return data.data;
  },
};