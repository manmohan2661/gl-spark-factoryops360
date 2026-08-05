import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { canAccess } from '@/utils/permissions';

interface ProtectedRouteProps {
  children: React.ReactNode;
  resource?: string;
  nested?: boolean;
}

export function ProtectedRoute({ children, resource, nested = false }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (resource && !canAccess(user?.role ?? null, resource)) {
    return nested ? <Navigate to="/" replace /> : <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}