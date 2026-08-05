import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { X } from 'lucide-react';
import { cn } from '@/utils/cn';

type ToastVariant = 'default' | 'success' | 'warning' | 'destructive';

interface ToastItem {
  id: number;
  title: string;
  description?: string;
  variant?: ToastVariant;
}

interface ToastContextValue {
  toast: (payload: Omit<ToastItem, 'id'>) => void;
  dismiss: (id: number) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const dismiss = (id: number) => setToasts((current) => current.filter((toast) => toast.id !== id));

  const value = useMemo<ToastContextValue>(
    () => ({
      toast: (payload) => {
        const id = window.setTimeout(() => dismiss(id), 4500);
        setToasts((current) => [...current, { id, ...payload }]);
      },
      dismiss,
    }),
    [],
  );

  useEffect(() => {
    return () => setToasts([]);
  }, []);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="fixed bottom-4 right-4 z-[100] flex w-[92vw] max-w-sm flex-col gap-3 md:bottom-6 md:right-6">
        {toasts.map((item) => (
          <div
            key={item.id}
            className={cn(
              'rounded-2xl border bg-card p-4 shadow-2xl backdrop-blur animate-fadeIn',
              item.variant === 'success' && 'border-emerald-200 bg-emerald-50 text-emerald-950 dark:border-emerald-900 dark:bg-emerald-950/70 dark:text-emerald-50',
              item.variant === 'warning' && 'border-amber-200 bg-amber-50 text-amber-950 dark:border-amber-900 dark:bg-amber-950/70 dark:text-amber-50',
              item.variant === 'destructive' && 'border-red-200 bg-red-50 text-red-950 dark:border-red-900 dark:bg-red-950/70 dark:text-red-50',
            )}
          >
            <div className="flex items-start gap-3">
              <div className="min-w-0 flex-1">
                <p className="font-semibold">{item.title}</p>
                {item.description ? <p className="mt-1 text-sm opacity-90">{item.description}</p> : null}
              </div>
              <button type="button" onClick={() => dismiss(item.id)} className="rounded-full p-1 transition-colors hover:bg-black/5 dark:hover:bg-white/10">
                <X className="h-4 w-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);

  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }

  return context;
}