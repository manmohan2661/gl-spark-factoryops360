export type AuditAction = 'CREATE' | 'UPDATE' | 'DELETE' | 'LOGIN' | 'LOGOUT';

export interface AuditLogResponse {
  id: number;
  entityName: string;
  entityId: number;
  action: AuditAction;
  performedBy: string;
  performedAt: string;
  details: string;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardSummaryResponse {
  totalOrders: number;
  completedOrders: number;
  pendingOrders: number;
  totalProductionQuantity: number;
  overallHealthScore: number;
  generatedAt: string;
}

export interface ProductionDashboardResponse {
  totalMachines: number;
  operationalMachines: number;
  maintenancePending: number;
  runningBatches: number;
  completedBatches: number;
  machineUtilization: number;
}

export interface InventoryDashboardResponse {
  totalMaterials: number;
  lowStockMaterials: number;
  outOfStockMaterials: number;
  inventoryValue: number;
  totalWarehouses: number;
}

export interface SupplierDashboardResponse {
  totalSuppliers: number;
  activeSuppliers: number;
  averageSupplierRating: number;
  pendingDeliveries: number;
  completedDeliveries: number;
}

export interface QualityDashboardResponse {
  totalInspections: number;
  passedInspections: number;
  failedInspections: number;
  qualityPassRate: number;
  totalDefects: number;
}

export interface AlertDashboardResponse {
  activeAlerts: number;
  criticalAlerts: number;
  warningAlerts: number;
  acknowledgedAlerts: number;
  unAcknowledgedAlerts: number;
}

export interface SystemHealthResponse {
  overallStatus: string;
  overallHealthScore: number;
  totalServices: number;
  healthyServices: number;
  unhealthyServices: number;
  responseTime: number;
  lastHealthCheck: string;
}

export interface DashboardResponse {
  summary: DashboardSummaryResponse;
  production: ProductionDashboardResponse;
  inventory: InventoryDashboardResponse;
  supplier: SupplierDashboardResponse;
  quality: QualityDashboardResponse;
  alerts: AlertDashboardResponse;
  systemHealth: SystemHealthResponse;
}
export interface KpiItem {
  label: string;
  value: string | number;
  delta?: string;
  tone?: 'default' | 'success' | 'warning' | 'danger';
}

export interface DashboardOverview {
  production?: {
    totalOrders?: number;
    completedOrders?: number;
    pendingOrders?: number;
    efficiency?: number;
    machineHealth?: number;
    trend?: Array<{ name: string; value: number }>;
  };
  inventory?: {
    totalStock?: number;
    lowStockAlerts?: number;
    outOfStockMaterials?: number;
    warehouseOverview?: Array<{ name: string; value: number }>;
  };
  quality?: {
    totalInspections?: number;
    passPercentage?: number;
    defectCount?: number;
    qualityTrend?: Array<{ name: string; value: number }>;
  };
  supplier?: {
    performance?: number;
    rating?: number;
    deliveryStatus?: Array<{ name: string; value: number }>;
  };
  system?: {
    serviceHealth?: number;
    activeAlerts?: number;
  };
}

export interface AlertItem {
  id: string;
  type: 'CRITICAL' | 'WARNING' | 'INFO';
  title: string;
  description: string;
  timestamp: string;
}