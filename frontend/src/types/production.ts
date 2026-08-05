export type ProductionOrderStatus = 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'ON_HOLD';

export type BatchStatus = 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export type MachineStatus = 'OPERATIONAL' | 'UNDER_MAINTENANCE' | 'BREAKDOWN' | 'DECOMMISSIONED';

export type MaintenanceStatus = 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export type MaintenanceType = 'PREVENTIVE' | 'CORRECTIVE' | 'PREDICTIVE';

export interface ProductionOrderRequest {
  orderNumber: string;
  productName: string;
  quantityOrdered: number;
  quantityProduced: number;
  status: ProductionOrderStatus;
  priority: number;
  startDate: string | null;
  endDate: string | null;
}

export interface MachineRequest {
  machineCode: string;
  name: string;
  type: string;
  status: MachineStatus;
  location: string;
  installationDate: string | null;
}

export interface MachineMaintenanceRequest {
  maintenanceType: MaintenanceType;
  status: MaintenanceStatus;
  scheduledDate: string | null;
  completedDate: string | null;
  remarks: string;
  machineId: number;
}
export interface ShiftResponse {
  id: number;
  shiftName: string;
  startTime: string;
  endTime: string;
  supervisorName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductionOrderResponse {
  id: number;
  orderNumber: string;
  productName: string;
  quantityOrdered: number;
  quantityProduced: number;
  status: ProductionOrderStatus;
  priority: number;
  startDate: string;
  endDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductionBatchResponse {
  id: number;
  batchNumber: string;
  quantity: number;
  status: BatchStatus;
  startTime: string;
  endTime: string;
  productionOrderId: number;
  machineId: number;
  shiftId: number;
  createdAt: string;
  updatedAt: string;
}

export interface MachineResponse {
  id: number;
  machineCode: string;
  name: string;
  type: string;
  status: MachineStatus;
  location: string;
  installationDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface MachineMaintenanceResponse {
  id: number;
  maintenanceType: MaintenanceType;
  status: MaintenanceStatus;
  scheduledDate: string;
  completedDate: string;
  remarks: string;
  machineId: number;
  createdAt: string;
  updatedAt: string;
}
