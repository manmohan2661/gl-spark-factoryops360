import { cn } from '@/utils/cn';

function Separator({ className }: { className?: string }) {
  return <div className={cn('h-px w-full bg-border', className)} />;
}

export { Separator };