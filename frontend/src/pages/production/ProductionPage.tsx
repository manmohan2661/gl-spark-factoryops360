import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { productionApi } from '@/api/productionApi';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { ResourceTable } from '@/components/tables/ResourceTable';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Card, CardContent } from '@/components/ui/card';
import { formatDateTime } from '@/utils/date';
import type { ProductionOrderResponse } from '@/types/production';

export function ProductionPage() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const ordersQuery = useQuery({ queryKey: ['production-orders'], queryFn: productionApi.getProductionOrders });

  if (ordersQuery.isLoading) {
    return <LoadingState />;
  }

  if (ordersQuery.isError) {
    return (
      <ErrorState
        description="Production data could not be loaded from the API gateway."
        onRetry={() => {
          ordersQuery.refetch();
        }}
      />
    );
  }

  const orders = ordersQuery.data ?? [];

  const filteredOrders = orders.filter((order: ProductionOrderResponse) => {
    const haystack = `${order.orderNumber} ${order.productName} ${order.status}`.toLowerCase();
    return haystack.includes(search.toLowerCase());
  });

  const pageSize = 10;
  const totalPages = Math.max(1, Math.ceil(filteredOrders.length / pageSize));
  const pagedOrders = filteredOrders.slice(page * pageSize, (page + 1) * pageSize);

  const summary = {
    totalOrders: orders.length,
    completedOrders: orders.filter((order) => order.status === 'COMPLETED').length,
    pendingOrders: orders.filter((order) => order.status !== 'COMPLETED').length,
    efficiency: (() => {
      const orderedQuantity = orders.reduce((total, order) => total + order.quantityOrdered, 0);
      const producedQuantity = orders.reduce((total, order) => total + order.quantityProduced, 0);
      return orderedQuantity > 0 ? Math.round((producedQuantity / orderedQuantity) * 100) : 0;
    })(),
  };

  return (
    <div className="space-y-6">
      <PageHeader title="Production Management" description="Analyze production orders, line throughput, and equipment efficiency." />

      <div className="grid gap-4 md:grid-cols-4">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total orders</p><p className="mt-2 text-3xl font-semibold">{summary.totalOrders}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Completed</p><p className="mt-2 text-3xl font-semibold">{summary.completedOrders}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Pending</p><p className="mt-2 text-3xl font-semibold">{summary.pendingOrders}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Efficiency</p><p className="mt-2 text-3xl font-semibold">{summary.efficiency}%</p></CardContent></Card>
      </div>

      <ResourceTable
        title="Production orders"
        data={pagedOrders}
        columns={[
          { header: 'Order', cell: (item) => <div><div className="font-medium">{item.orderNumber}</div><div className="text-xs text-muted-foreground">{item.productName}</div></div> },
          { header: 'Quantity', cell: (item) => item.quantityOrdered },
          { header: 'Completed', cell: (item) => item.quantityProduced },
          { header: 'Priority', cell: (item) => item.priority },
          { header: 'Status', cell: (item) => <StatusBadge value={item.status} /> },
          { header: 'Updated', cell: (item) => formatDateTime(item.updatedAt) },
        ]}
        search={search}
        onSearchChange={(value) => {
          setPage(0);
          setSearch(value);
        }}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
        emptyMessage="No production orders were returned by the backend."
      />
    </div>
  );
}