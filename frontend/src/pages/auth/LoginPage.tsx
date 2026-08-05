import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, useLocation } from 'react-router-dom';
import { AlertTriangle, LogIn } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [submitError, setSubmitError] = useState<string | null>(null);

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (values: LoginFormValues) => {
    try {
      setSubmitError(null);
      await login(values);
      const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/';
      navigate(from, { replace: true });
    } catch (error) {
      setSubmitError(error instanceof Error ? error.message : 'Login failed. Please verify your credentials.');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-800 px-4 py-10 text-slate-100">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] max-w-7xl items-center">
        <div className="grid w-full gap-8 lg:grid-cols-[1.15fr_0.85fr]">
          <div className="flex flex-col justify-center gap-6 rounded-3xl border border-white/10 bg-white/5 p-8 shadow-soft backdrop-blur">
            <div className="space-y-4">
              <p className="text-xs uppercase tracking-[0.35em] text-cyan-300">FactoryOps360</p>
              <h1 className="max-w-2xl text-4xl font-semibold tracking-tight md:text-6xl">Industrial operations control for modern manufacturing plants.</h1>
              <p className="max-w-2xl text-sm leading-6 text-slate-300 md:text-base">
                Monitor production KPIs, inventory health, quality metrics, supplier performance, and active alerts from one secure enterprise workspace.
              </p>
            </div>

            <div className="grid gap-4 sm:grid-cols-3">
              {[
                ['Command center', 'KPI dense dashboard'],
                ['Role aware', 'Secure access control'],
                ['Real time', 'API-driven operations'],
              ].map(([title, description]) => (
                <div key={title} className="rounded-2xl border border-white/10 bg-black/20 p-4">
                  <p className="text-sm font-semibold">{title}</p>
                  <p className="mt-1 text-xs text-slate-300">{description}</p>
                </div>
              ))}
            </div>
          </div>

          <Card className="self-center border-slate-200/20 bg-white/95 text-slate-900 shadow-2xl">
            <CardHeader>
              <CardTitle className="text-2xl">Sign in</CardTitle>
              <CardDescription>Use your backend-issued credentials to access the factory operations portal.</CardDescription>
            </CardHeader>
            <CardContent>
              <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
                <div className="space-y-2">
                  <label className="text-sm font-medium">Username</label>
                  <Input {...register('username')} placeholder="Enter username" />
                  {errors.username ? <p className="text-xs text-red-600">{errors.username.message}</p> : null}
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium">Password</label>
                  <Input {...register('password')} type="password" placeholder="Enter password" />
                  {errors.password ? <p className="text-xs text-red-600">{errors.password.message}</p> : null}
                </div>

                {submitError ? (
                  <div className="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                    <AlertTriangle className="mt-0.5 h-4 w-4" />
                    <span>{submitError}</span>
                  </div>
                ) : null}

                <Button className="w-full" type="submit" disabled={isSubmitting}>
                  <LogIn className="h-4 w-4" />
                  {isSubmitting ? 'Signing in...' : 'Sign in'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}