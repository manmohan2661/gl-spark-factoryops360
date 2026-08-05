import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { DefectResponse, QualityInspectionResponse, QualityInspectionRequest, DefectRequest } from '@/types/quality';

export const qualityApi = {
  getQualityInspections: async () => {
    const { data } = await apiClient.get<ApiResponse<QualityInspectionResponse[]>>('/api/v1/quality-inspections');
    return data.data;
  },
  getQualityInspectionById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<QualityInspectionResponse>>(`/api/v1/quality-inspections/${id}`);
    return data.data;
  },
  createQualityInspection: async (payload: QualityInspectionRequest) => {
    const { data } = await apiClient.post<ApiResponse<QualityInspectionResponse>>('/api/v1/quality-inspections', payload);
    return data.data;
  },
  updateQualityInspection: async (id: number, payload: QualityInspectionRequest) => {
    const { data } = await apiClient.put<ApiResponse<QualityInspectionResponse>>(`/api/v1/quality-inspections/${id}`, payload);
    return data.data;
  },
  deleteQualityInspection: async (id: number) => {
    await apiClient.delete(`/api/v1/quality-inspections/${id}`);
  },
  getDefects: async () => {
    const { data } = await apiClient.get<ApiResponse<DefectResponse[]>>('/api/v1/defects');
    return data.data;
  },
  getDefectById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<DefectResponse>>(`/api/v1/defects/${id}`);
    return data.data;
  },
  createDefect: async (payload: DefectRequest) => {
    const { data } = await apiClient.post<ApiResponse<DefectResponse>>('/api/v1/defects', payload);
    return data.data;
  },
  updateDefect: async (id: number, payload: DefectRequest) => {
    const { data } = await apiClient.put<ApiResponse<DefectResponse>>(`/api/v1/defects/${id}`, payload);
    return data.data;
  },
  deleteDefect: async (id: number) => {
    await apiClient.delete(`/api/v1/defects/${id}`);
  },
};