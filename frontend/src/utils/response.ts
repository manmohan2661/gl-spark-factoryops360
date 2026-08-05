export function extractCollection<T>(response: { content?: T[]; items?: T[]; data?: T[] } | undefined): T[] {
  if (!response) {
    return [];
  }

  if (Array.isArray(response.content)) {
    return response.content;
  }

  if (Array.isArray(response.items)) {
    return response.items;
  }

  if (Array.isArray(response.data)) {
    return response.data;
  }

  return [];
}

export function extractPageCount(response: { totalPages?: number } | undefined) {
  return response?.totalPages ?? 1;
}