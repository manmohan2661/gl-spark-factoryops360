export interface ApiErrorResponse {
  message?: string;
  error?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  statusCode: number;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ApiListResponse<T> {
  data?: T[];
  items?: T[];
  content?: T[];
}