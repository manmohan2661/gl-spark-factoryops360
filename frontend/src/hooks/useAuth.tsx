import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { useMutation } from '@tanstack/react-query';
import { authApi } from '@/api/authApi';
import { storage } from '@/utils/storage';
import type { AuthUser, LoginResponse } from '@/types/auth';
import { useToast } from '@/components/ui/toast';
import { normalizeRole } from '@/utils/permissions';

interface LoginPayload {
  username: string;
  password: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (payload: LoginPayload) => Promise<LoginResponse>;
  logout: () => void;
  handleSessionExpired: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const { toast } = useToast();
  const [user, setUser] = useState<AuthUser | null>(() => {
    const token = storage.getToken();
    const username = sessionStorage.getItem('factoryops360.username');
    const role = sessionStorage.getItem('factoryops360.role');
    const normalizedRole = normalizeRole(role);

    if (!token || !username || !normalizedRole) {
      return null;
    }

    return { username, role: normalizedRole };
  });

  const [token, setToken] = useState<string | null>(() => storage.getToken());

  useEffect(() => {
    if (token) {
      storage.setToken(token);
    } else {
      storage.clearToken();
    }
  }, [token]);

  useEffect(() => {
    const handleExpiry = () => {
      setUser(null);
      setToken(null);
      sessionStorage.removeItem('factoryops360.username');
      sessionStorage.removeItem('factoryops360.role');
    };

    window.addEventListener('factoryops360:session-expired', handleExpiry);

    return () => window.removeEventListener('factoryops360:session-expired', handleExpiry);
  }, []);

  const loginMutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (response, variables) => {
      setToken(response.token);
      const nextUser = { username: response.username || variables.username, role: response.role };
      setUser(nextUser);
      sessionStorage.setItem('factoryops360.username', nextUser.username);
      sessionStorage.setItem('factoryops360.role', nextUser.role);
      toast({ title: 'Signed in', description: `Welcome back, ${nextUser.username}.`, variant: 'success' });
    },
    onError: () => {
      toast({ title: 'Sign in failed', description: 'Verify your credentials and try again.', variant: 'destructive' });
    },
  });

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token && user),
      login: async (payload: LoginPayload) => loginMutation.mutateAsync(payload),
      logout: () => {
        setUser(null);
        setToken(null);
        sessionStorage.removeItem('factoryops360.username');
        sessionStorage.removeItem('factoryops360.role');
        storage.clearToken();
        toast({ title: 'Signed out', description: 'Your session has been cleared.' });
      },
      handleSessionExpired: () => {
        setUser(null);
        setToken(null);
        sessionStorage.removeItem('factoryops360.username');
        sessionStorage.removeItem('factoryops360.role');
        storage.clearToken();
        toast({ title: 'Session expired', description: 'Please sign in again.', variant: 'warning' });
      },
    }),
    [loginMutation, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
}