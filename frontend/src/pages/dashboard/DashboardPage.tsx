import { useQuery } from '@tanstack/react-query';
import { BellRing, CheckCircle2, Factory, ShieldAlert, Truck, Warehouse } from 'lucide-react';
import { alertApi } from '@/api/alertApi';
import { dashboardApi } from '@/api/dashboardApi';
import { MetricCard } from '@/components/common/MetricCard';
import { EmptyState } from '@/components/common/EmptyState';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { DistributionChart } from '@/components/charts/DistributionChart';
import { TrendChart } from '@/components/charts/TrendChart';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

export function DashboardPage() {
  const dashboardQuery = useQuery({ queryKey: ['dashboard'], queryFn: dashboardApi.getDashboard });
  const alertsQuery = useQuery({ queryKey: ['alerts'], queryFn: alertApi.getAlerts });

  const isLoading = dashboardQuery.isLoading || alertsQuery.isLoading;
  const isError = dashboardQuery.isError || alertsQuery.isError;

  if (isLoading) {
    return <LoadingState />;
  }

  if (isError) {
    return (
      <ErrorState
        description="The command center could not load one or more backend resources. Retry after confirming the API gateway is available."
        onRetry={() => {
          dashboardQuery.refetch();
          alertsQuery.refetch();
        }}
      />
    );
  }

  const dashboard = dashboardQuery.data;
  const alerts = alertsQuery.data ?? [];

  const summary = dashboard?.summary;
  const production = dashboard?.production;
  const inventory = dashboard?.inventory;
  const quality = dashboard?.quality;
  const supplier = dashboard?.supplier;
  const systemHealth = dashboard?.systemHealth;

  const productionSnapshot = production
    ? [
        { name: 'Machines', value: production.totalMachines },
        { name: 'Operational', value: production.operationalMachines },
        { name: 'Maintenance', value: production.maintenancePending },
        { name: 'Running', value: production.runningBatches },
        { name: 'Completed', value: production.completedBatches },
      ]
    : [];

  const inventorySnapshot = inventory
    ? [
        { name: 'Materials', value: inventory.totalMaterials },
        { name: 'Low stock', value: inventory.lowStockMaterials },
        { name: 'Out of stock', value: inventory.outOfStockMaterials },
        { name: 'Warehouses', value: inventory.totalWarehouses },
      ]
    : [];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Enterprise Factory Command Center"
        description="Live production, quality, supply chain, and system health overview sourced from the Spring Boot gateway."
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        <MetricCard label="Total orders" value={summary?.totalOrders ?? '—'} delta={summary?.pendingOrders !== undefined ? `${summary.pendingOrders} pending` : undefined} tone="default" />
        <MetricCard label="Completed orders" value={summary?.completedOrders ?? '—'} delta={summary?.totalProductionQuantity !== undefined ? `${summary.totalProductionQuantity} produced` : undefined} tone="success" />
        <MetricCard label="Inventory materials" value={inventory?.totalMaterials ?? '—'} delta={inventory?.lowStockMaterials !== undefined ? `${inventory.lowStockMaterials} low stock` : undefined} tone="warning" />
        <MetricCard label="Quality pass rate" value={quality?.qualityPassRate !== undefined ? `${quality.qualityPassRate}%` : '—'} delta={quality?.totalDefects !== undefined ? `${quality.totalDefects} defects` : undefined} tone="success" />
        <MetricCard label="Supplier rating" value={supplier?.averageSupplierRating !== undefined ? supplier.averageSupplierRating : '—'} delta={systemHealth?.overallStatus ?? undefined} tone="default" />
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <TrendChart title="Production snapshot" data={productionSnapshot} />
        <DistributionChart title="Inventory snapshot" data={inventorySnapshot} />
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><ShieldAlert className="h-5 w-5" /> System Health</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4 md:grid-cols-2">
            <div className="rounded-2xl border bg-muted/30 p-4">
              <p className="text-sm text-muted-foreground">Service health</p>
              <p className="mt-2 text-3xl font-semibold">{systemHealth?.overallHealthScore ?? '—'}%</p>
            </div>
            <div className="rounded-2xl border bg-muted/30 p-4">
              <p className="text-sm text-muted-foreground">Active alerts</p>
              <p className="mt-2 text-3xl font-semibold">{dashboard?.alerts.activeAlerts ?? '—'}</p>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><BellRing className="h-5 w-5" /> Active alerts</CardTitle>
          </CardHeader>
          <CardContent>
            {alerts.length === 0 ? (
              <EmptyState title="No active alerts" description="The alert center has not received any events from the gateway." />
            ) : (
              <div className="overflow-hidden rounded-xl border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Type</TableHead>
                      <TableHead>Title</TableHead>
                      <TableHead>Description</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {alerts.slice(0, 5).map((alert) => (
                      <TableRow key={alert.id}>
                        <TableCell>
                          <Badge variant={alert.severity === 'CRITICAL' ? 'danger' : alert.severity === 'HIGH' ? 'warning' : 'secondary'}>{alert.severity}</Badge>
                        </TableCell>
                        <TableCell className="font-medium">{alert.title}</TableCell>
                        <TableCell className="text-muted-foreground">{alert.message}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {[
          { title: 'Machine health', icon: Factory, value: production?.machineUtilization ?? '—' },
          { title: 'Warehouses', icon: Warehouse, value: inventory?.totalWarehouses ?? '—' },
          { title: 'Inspections', icon: CheckCircle2, value: quality?.totalInspections ?? '—' },
          { title: 'Supplier rating', icon: Truck, value: supplier?.averageSupplierRating ?? '—' },
        ].map(({ title, icon: Icon, value }) => (
          <Card key={title}>
            <CardContent className="flex items-center justify-between p-6">
              <div>
                <p className="text-sm text-muted-foreground">{title}</p>
                <p className="mt-2 text-2xl font-semibold">{value}</p>
              </div>
              <Icon className="h-6 w-6 text-primary" />
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}