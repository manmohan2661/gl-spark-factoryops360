export type InspectionResult = 'PASS' | 'FAIL' | 'PENDING';

export type DefectSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface QualityInspectionRequest {
  inspectorName: string;
  inspectionDate: string | null;
  result: InspectionResult;
  remarks: string;
  productionBatchId: number;
}

export interface DefectRequest {
  defectType: string;
  severity: DefectSeverity;
  description: string;
  reportedDate: string | null;
  resolved: boolean;
  qualityInspectionId: number;
}

export interface QualityInspectionResponse {
  id: number;
  inspectorName: string;
  inspectionDate: string;
  result: InspectionResult;
  remarks: string;
  productionBatchId: number;
  createdAt: string;
  updatedAt: string;
}

export interface DefectResponse {
  id: number;
  defectType: string;
  severity: DefectSeverity;
  description: string;
  reportedDate: string;
  resolved: boolean;
  qualityInspectionId: number;
  createdAt: string;
  updatedAt: string;
}
