import { ArrowDownRight, ArrowUpRight, Minus } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { cn } from '@/utils/cn';

interface MetricCardProps {
  label: string;
  value: string | number;
  delta?: string;
  tone?: 'default' | 'success' | 'warning' | 'danger';
}

const toneClassName: Record<NonNullable<MetricCardProps['tone']>, string> = {
  default: 'text-foreground',
  success: 'text-emerald-600 dark:text-emerald-400',
  warning: 'text-amber-600 dark:text-amber-400',
  danger: 'text-red-600 dark:text-red-400',
};

export function MetricCard({ label, value, delta, tone = 'default' }: MetricCardProps) {
  const positive = delta ? delta.trim().startsWith('+') : false;
  const TrendIcon = delta ? (positive ? ArrowUpRight : ArrowDownRight) : Minus;

  return (
    <Card>
      <CardHeader className="pb-2">
        <CardDescription>{label}</CardDescription>
        <CardTitle className={cn('text-3xl', toneClassName[tone])}>{value}</CardTitle>
      </CardHeader>
      <CardContent className="flex items-center gap-2 pt-0 text-sm text-muted-foreground">
        <TrendIcon className="h-4 w-4" />
        <span>{delta ?? 'No trend available'}</span>
      </CardContent>
    </Card>
  );
}