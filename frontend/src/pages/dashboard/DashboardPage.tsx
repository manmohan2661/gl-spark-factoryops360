import { useQuery } from '@tanstack/react-query';
import {
  Activity,
  AlertTriangle,
  BellRing,
  CheckCircle2,
  Factory,
  PackageSearch,
  ShieldAlert,
  Truck,
  Warehouse,
  History,
} from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { dashboardApi } from '@/api/dashboardApi';
import { MetricCard } from '@/components/common/MetricCard';
import { EmptyState } from '@/components/common/EmptyState';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { DistributionChart } from '@/components/charts/DistributionChart';
import { TrendChart } from '@/components/charts/TrendChart';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { formatDateTime } from '@/utils/date';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { cn } from '@/utils/cn';

export function DashboardPage() {
  const { user } = useAuth();
  
  const dashboardQuery = useQuery({ queryKey: ['dashboard'], queryFn: dashboardApi.getDashboard });
  const auditLogsQuery = useQuery({ queryKey: ['audit-logs'], queryFn: dashboardApi.getAuditLogs });

  const isLoading = dashboardQuery.isLoading || auditLogsQuery.isLoading;
  const isError = dashboardQuery.isError || auditLogsQuery.isError;

  if (isLoading) return <LoadingState />;

  if (isError) {
    return (
      <ErrorState
        description="The Command Center could not connect to the API Gateway. Some services may be offline."
        onRetry={() => {
          dashboardQuery.refetch();
          auditLogsQuery.refetch();
        }}
      />
    );
  }

  const dashboard = dashboardQuery.data;
  const auditLogs = auditLogsQuery.data ?? [];
  const role = user?.role ?? 'VIEWER';

  const {
    summary,
    production,
    inventory,
    quality,
    supplier,
    alerts,
    systemHealth,
  } = dashboard || {};

  const getHealthColor = (score?: number) => {
    if (score === undefined) return 'text-muted-foreground';
    if (score >= 90) return 'text-green-600';
    if (score >= 70) return 'text-amber-500';
    return 'text-red-600';
  };

  const productionSnapshot = production ? [
    { name: 'Operational', value: production.operationalMachines },
    { name: 'Maintenance', value: production.maintenancePending },
    { name: 'Running Batches', value: production.runningBatches },
    { name: 'Completed Batches', value: production.completedBatches },
  ] : [];

  const inventorySnapshot = inventory ? [
    { name: 'Adequate Stock', value: inventory.totalMaterials - (inventory.lowStockMaterials + inventory.outOfStockMaterials) },
    { name: 'Low Stock', value: inventory.lowStockMaterials },
    { name: 'Out of Stock', value: inventory.outOfStockMaterials },
  ] : [];

  const showProduction = role === 'ADMIN' || role === 'PRODUCTION_MANAGER';
  const showInventory = role === 'ADMIN' || role === 'INVENTORY_MANAGER';
  const showQuality = role === 'ADMIN' || role === 'QUALITY_INSPECTOR';
  const showSupplier = role === 'ADMIN' || role === 'SUPPLIER_MANAGER';

  return (
    <div className="space-y-8 pb-8">
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-bold tracking-tight">Factory Command Center</h1>
        <p className="text-muted-foreground">Live monitoring and industrial telemetry overview.</p>
      </div>

      {/* Factory Health Command Center */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card className="border-l-4 border-l-primary shadow-sm">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Overall Health</p>
                <div className="flex items-baseline gap-2 mt-2">
                  <h2 className={cn("text-4xl font-bold tracking-tighter", getHealthColor(systemHealth?.overallHealthScore))}>
                    {systemHealth?.overallHealthScore ?? '—'}
                  </h2>
                  <span className="text-xl font-medium text-muted-foreground">/ 100</span>
                </div>
              </div>
              <Activity className={cn("h-10 w-10 opacity-20", getHealthColor(systemHealth?.overallHealthScore))} />
            </div>
            <p className="mt-4 text-xs font-medium text-muted-foreground flex items-center gap-1">
              {systemHealth?.overallStatus === 'HEALTHY' ? <CheckCircle2 className="h-3 w-3 text-green-600" /> : <AlertTriangle className="h-3 w-3 text-red-600" />}
              {systemHealth?.healthyServices} of {systemHealth?.totalServices} services online
            </p>
          </CardContent>
        </Card>

        {showProduction && (
          <Card className="shadow-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Production Efficiency</p>
                  <h2 className="mt-2 text-3xl font-bold">{summary?.totalOrders ?? '—'} <span className="text-lg font-normal text-muted-foreground">Orders</span></h2>
                </div>
                <Factory className="h-8 w-8 text-blue-500/20" />
              </div>
              <p className="mt-4 text-xs font-medium text-muted-foreground">
                {summary?.completedOrders} completed • {summary?.pendingOrders} pending
              </p>
            </CardContent>
          </Card>
        )}

        {showQuality && (
          <Card className="shadow-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Quality Pass Rate</p>
                  <h2 className={cn("mt-2 text-3xl font-bold", getHealthColor(quality?.qualityPassRate))}>{quality?.qualityPassRate ?? '—'}%</h2>
                </div>
                <CheckCircle2 className="h-8 w-8 text-green-500/20" />
              </div>
              <p className="mt-4 text-xs font-medium text-muted-foreground">
                {quality?.passedInspections} passed out of {quality?.totalInspections}
              </p>
            </CardContent>
          </Card>
        )}

        {showInventory && (
          <Card className="shadow-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Inventory Risk</p>
                  <h2 className={cn("mt-2 text-3xl font-bold", inventory?.outOfStockMaterials ? "text-red-600" : "")}>
                    {inventory?.outOfStockMaterials ?? '—'} <span className="text-lg font-normal text-muted-foreground">Empty</span>
                  </h2>
                </div>
                <Warehouse className="h-8 w-8 text-amber-500/20" />
              </div>
              <p className="mt-4 text-xs font-medium text-muted-foreground">
                {inventory?.lowStockMaterials} items low on stock
              </p>
            </CardContent>
          </Card>
        )}
      </div>

      {/* Role-Specific KPIs */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {showProduction && (
          <>
            <MetricCard label="Machine Utilization" value={production?.machineUtilization ? `${production.machineUtilization}%` : '—'} tone="default" />
            <MetricCard label="Total Production Qty" value={summary?.totalProductionQuantity ?? '—'} tone="default" />
          </>
        )}
        {showSupplier && (
          <>
            <MetricCard label="Active Suppliers" value={supplier?.activeSuppliers ?? '—'} tone="default" />
            <MetricCard label="Avg Supplier Rating" value={supplier?.averageSupplierRating ?? '—'} tone={supplier?.averageSupplierRating && supplier.averageSupplierRating >= 4 ? 'success' : 'warning'} />
          </>
        )}
        {showInventory && !showProduction && (
          <>
            <MetricCard label="Total Warehouses" value={inventory?.totalWarehouses ?? '—'} tone="default" />
            <MetricCard label="Total Materials" value={inventory?.totalMaterials ?? '—'} tone="default" />
          </>
        )}
      </div>

      {/* Charts Section */}
      <div className="grid gap-6 xl:grid-cols-2">
        {showProduction && <TrendChart title="Production Snapshot" data={productionSnapshot} />}
        {showInventory && <DistributionChart title="Inventory Stock Distribution" data={inventorySnapshot} />}
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        {/* Alerts Widget */}
        <Card className="flex flex-col h-full shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <div className="space-y-1">
              <CardTitle className="flex items-center gap-2 text-lg">
                <BellRing className="h-5 w-5" /> 
                Active System Alerts
              </CardTitle>
              <CardDescription>
                {alerts?.activeAlerts ?? 0} active • {alerts?.criticalAlerts ?? 0} critical
              </CardDescription>
            </div>
          </CardHeader>
          <CardContent className="flex-1">
            <div className="rounded-xl border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Severity</TableHead>
                    <TableHead>Overview</TableHead>
                    <TableHead className="text-right">Action</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {(!alerts || alerts.activeAlerts === 0) ? (
                    <TableRow>
                      <TableCell colSpan={3} className="h-24 text-center text-muted-foreground">
                        All systems normal. No active alerts.
                      </TableCell>
                    </TableRow>
                  ) : (
                    <>
                      {alerts.criticalAlerts > 0 && (
                        <TableRow>
                          <TableCell><Badge variant="danger">CRITICAL</Badge></TableCell>
                          <TableCell className="font-medium text-sm">Critical failures detected in infrastructure.</TableCell>
                          <TableCell className="text-right"><Link to="/alerts" className="text-sm font-medium hover:underline text-primary">View</Link></TableCell>
                        </TableRow>
                      )}
                      {alerts.warningAlerts > 0 && (
                        <TableRow>
                          <TableCell><Badge variant="warning">WARNING</Badge></TableCell>
                          <TableCell className="font-medium text-sm">Non-critical warnings reported.</TableCell>
                          <TableCell className="text-right"><Link to="/alerts" className="text-sm font-medium hover:underline text-primary">View</Link></TableCell>
                        </TableRow>
                      )}
                      <TableRow>
                        <TableCell colSpan={3} className="text-center bg-muted/20 py-4">
                          <Link to="/alerts" className="text-xs text-muted-foreground hover:underline font-medium">View all {alerts.activeAlerts} alerts in Alert Center &rarr;</Link>
                        </TableCell>
                      </TableRow>
                    </>
                  )}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>

        {/* Audit Log / Recent Activity Widget */}
        <Card className="flex flex-col h-full shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <div className="space-y-1">
              <CardTitle className="flex items-center gap-2 text-lg">
                <History className="h-5 w-5" /> 
                Recent Activity
              </CardTitle>
              <CardDescription>Latest audit trail events</CardDescription>
            </div>
          </CardHeader>
          <CardContent className="flex-1">
            {auditLogs.length === 0 ? (
              <EmptyState title="No activity" description="No recent actions have been logged to the audit trail." />
            ) : (
              <div className="rounded-xl border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Action</TableHead>
                      <TableHead>Entity</TableHead>
                      <TableHead>User</TableHead>
                      <TableHead className="text-right">Time</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {auditLogs.slice(0, 5).map((log) => (
                      <TableRow key={log.id}>
                        <TableCell>
                          <Badge variant="outline" className={cn(
                            log.action === 'CREATE' && "border-green-200 text-green-700 bg-green-50",
                            log.action === 'UPDATE' && "border-blue-200 text-blue-700 bg-blue-50",
                            log.action === 'DELETE' && "border-red-200 text-red-700 bg-red-50",
                          )}>
                            {log.action}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-sm font-medium">
                          {log.entityName} <span className="text-muted-foreground font-normal">#{log.entityId}</span>
                        </TableCell>
                        <TableCell className="text-sm text-muted-foreground">{log.performedBy}</TableCell>
                        <TableCell className="text-right text-xs text-muted-foreground whitespace-nowrap">
                          {new Date(log.performedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}