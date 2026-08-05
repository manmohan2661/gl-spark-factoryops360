export type SupplierStatus = 'ACTIVE' | 'INACTIVE' | 'BLACKLISTED';

export interface SupplierRequest {
  code: string;
  name: string;
  contactPerson: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
  status: SupplierStatus;
  rating: number;
}

export interface SupplierPerformanceRequest {
  evaluationPeriod: string;
  onTimeDeliveryRate: number;
  qualityScore: number;
  defectRate: number;
  remarks: string;
  evaluatedAt: string | null;
  supplierId: number;
}

export interface SupplierResponse {
  id: number;
  code: string;
  name: string;
  contactPerson: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
  status: SupplierStatus;
  rating: number;
  createdAt: string;
  updatedAt: string;
}

export interface SupplierPerformanceResponse {
  id: number;
  evaluationPeriod: string;
  onTimeDeliveryRate: number;
  qualityScore: number;
  defectRate: number;
  remarks: string;
  evaluatedAt: string;
  supplierId: number;
  createdAt: string;
  updatedAt: string;
}
