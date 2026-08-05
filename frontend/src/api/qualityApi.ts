import { apiClient } from '@/api/axios';
import type { ApiResponse } from '@/types/auth';
import type { DefectResponse, QualityInspectionResponse } from '@/types/quality';

export const qualityApi = {
  getQualityInspections: async () => {
    const { data } = await apiClient.get<ApiResponse<QualityInspectionResponse[]>>('/api/v1/quality-inspections');
    return data.data;
  },
  getQualityInspectionById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<QualityInspectionResponse>>(`/api/v1/quality-inspections/${id}`);
    return data.data;
  },
  getDefects: async () => {
    const { data } = await apiClient.get<ApiResponse<DefectResponse[]>>('/api/v1/defects');
    return data.data;
  },
  getDefectById: async (id: number) => {
    const { data } = await apiClient.get<ApiResponse<DefectResponse>>(`/api/v1/defects/${id}`);
    return data.data;
  },
};