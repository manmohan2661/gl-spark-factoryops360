import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { AppShell } from '@/components/layouts/AppShell';
import { ProtectedRoute } from '@/components/layouts/ProtectedRoute';
import { LoginPage } from '@/pages/auth/LoginPage';
import { DashboardPage } from '@/pages/dashboard/DashboardPage';
import { InventoryPage } from '@/pages/inventory/InventoryPage';
import { ProductionPage } from '@/pages/production/ProductionPage';
import { QualityPage } from '@/pages/quality/QualityPage';
import { SupplierPage } from '@/pages/supplier/SupplierPage';
import { AlertCenterPage } from '@/pages/alerts/AlertCenterPage';
import { ProfilePage } from '@/pages/profile/ProfilePage';
import { NotFoundPage } from '@/pages/system/NotFoundPage';

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute resource="dashboard">
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route
          index
          element={
            <ProtectedRoute resource="dashboard" nested>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="inventory"
          element={
            <ProtectedRoute resource="inventory" nested>
              <InventoryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="production"
          element={
            <ProtectedRoute resource="production" nested>
              <ProductionPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="quality"
          element={
            <ProtectedRoute resource="quality" nested>
              <QualityPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="suppliers"
          element={
            <ProtectedRoute resource="suppliers" nested>
              <SupplierPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="alerts"
          element={
            <ProtectedRoute resource="alerts" nested>
              <AlertCenterPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="profile"
          element={
            <ProtectedRoute resource="profile" nested>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}