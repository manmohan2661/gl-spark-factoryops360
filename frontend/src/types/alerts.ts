export type AlertSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED';

export type NotificationChannel = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

export type AuditAction = 'CREATE' | 'UPDATE' | 'DELETE' | 'LOGIN' | 'LOGOUT';

export interface AlertResponse {
  id: number;
  title: string;
  message: string;
  severity: AlertSeverity;
  sourceService: string;
  triggeredAt: string;
  acknowledged: boolean;
  acknowledgedBy: string;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationResponse {
  id: number;
  recipient: string;
  title: string;
  message: string;
  channel: NotificationChannel;
  status: NotificationStatus;
  sentAt: string;
  createdAt: string;
  updatedAt: string;
}

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
