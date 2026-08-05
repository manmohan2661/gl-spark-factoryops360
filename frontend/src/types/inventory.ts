export type UnitOfMeasure = 'KG' | 'GRAM' | 'LITER' | 'MILLILITER' | 'PIECE' | 'BOX' | 'METER';

export type TransactionType = 'INBOUND' | 'OUTBOUND' | 'ADJUSTMENT' | 'TRANSFER';

export interface WarehouseRequest {
  code: string;
  name: string;
  location: string;
  capacity: number;
  active: boolean;
}

export interface MaterialRequest {
  code: string;
  name: string;
  description: string;
  unitOfMeasure: UnitOfMeasure;
  category: string;
  reorderLevel: number;
  active: boolean;
}

export interface InventoryRequest {
  quantityAvailable: number;
  quantityReserved: number;
  materialId: number;
  warehouseId: number;
}

export interface WarehouseResponse {
  id: number;
  code: string;
  name: string;
  location: string;
  capacity: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface MaterialResponse {
  id: number;
  code: string;
  name: string;
  description: string;
  unitOfMeasure: UnitOfMeasure;
  category: string;
  reorderLevel: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface InventoryResponse {
  id: number;
  quantityAvailable: number;
  quantityReserved: number;
  lastUpdated: string;
  materialId: number;
  warehouseId: number;
  createdAt: string;
  updatedAt: string;
}

export interface InventoryTransactionResponse {
  id: number;
  transactionType: TransactionType;
  quantity: number;
  referenceNumber: string;
  transactionDate: string;
  remarks: string;
  inventoryId: number;
  createdAt: string;
  updatedAt: string;
}
