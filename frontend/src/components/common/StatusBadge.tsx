import { Badge } from '@/components/ui/badge';

export function StatusBadge({ value }: { value: string }) {
  const normalized = value.toUpperCase();

  if (normalized.includes('CRITICAL') || normalized.includes('OUT_OF_STOCK') || normalized.includes('FAIL') || normalized.includes('DELAYED') || normalized.includes('PAUSED')) {
    return <Badge variant="danger">{value}</Badge>;
  }

  if (normalized.includes('WARNING') || normalized.includes('LOW') || normalized.includes('RISK') || normalized.includes('REWORK')) {
    return <Badge variant="warning">{value}</Badge>;
  }

  if (normalized.includes('PASS') || normalized.includes('COMPLETED') || normalized.includes('ON_TIME') || normalized.includes('IN_STOCK') || normalized.includes('RUNNING')) {
    return <Badge variant="success">{value}</Badge>;
  }

  return <Badge variant="secondary">{value}</Badge>;
}