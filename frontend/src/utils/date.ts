export function formatDateTime(value: string) {
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

export function formatPercentage(value?: number) {
  if (value === undefined || Number.isNaN(value)) {
    return '—';
  }

  return `${value}%`;
}