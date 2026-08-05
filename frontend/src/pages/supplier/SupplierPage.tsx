import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { supplierApi } from '@/api/supplierApi';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { ResourceTable } from '@/components/tables/ResourceTable';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Card, CardContent } from '@/components/ui/card';
import { formatDateTime } from '@/utils/date';
import type { SupplierPerformanceResponse, SupplierResponse } from '@/types/supplier';

interface SupplierRowView {
  id: number;
  supplierCode: string;
  supplierName: string;
  rating: number;
  deliveryStatus: 'ON_TIME' | 'DELAYED' | 'AT_RISK';
  performance: number;
  updatedAt: string;
}

function deriveDeliveryStatus(onTimeDeliveryRate: number) {
  if (onTimeDeliveryRate >= 90) {
    return 'ON_TIME' as const;
  }

  if (onTimeDeliveryRate >= 75) {
    return 'AT_RISK' as const;
  }

  return 'DELAYED' as const;
}

export function SupplierPage() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const suppliersQuery = useQuery({ queryKey: ['suppliers'], queryFn: supplierApi.getSuppliers });
  const performancesQuery = useQuery({ queryKey: ['supplier-performances'], queryFn: supplierApi.getSupplierPerformances });

  if (suppliersQuery.isLoading || performancesQuery.isLoading) {
    return <LoadingState />;
  }

  if (suppliersQuery.isError || performancesQuery.isError) {
    return (
      <ErrorState
        description="Supplier data could not be loaded from the API gateway."
        onRetry={() => {
          suppliersQuery.refetch();
          performancesQuery.refetch();
        }}
      />
    );
  }

  const suppliers = suppliersQuery.data ?? [];
  const performances = performancesQuery.data ?? [];

  const latestPerformanceBySupplier = performances.reduce((map, performance: SupplierPerformanceResponse) => {
    const existing = map.get(performance.supplierId);
    if (!existing || new Date(performance.evaluatedAt).getTime() > new Date(existing.evaluatedAt).getTime()) {
      map.set(performance.supplierId, performance);
    }
    return map;
  }, new Map<number, SupplierPerformanceResponse>());

  const rows: SupplierRowView[] = suppliers.map((supplier: SupplierResponse) => {
    const performance = latestPerformanceBySupplier.get(supplier.id);
    const deliveryRate = performance?.onTimeDeliveryRate ?? 0;

    return {
      id: supplier.id,
      supplierCode: supplier.code,
      supplierName: supplier.name,
      rating: supplier.rating,
      performance: deliveryRate,
      deliveryStatus: deriveDeliveryStatus(deliveryRate),
      updatedAt: supplier.updatedAt,
    };
  });

  const filteredRows = rows.filter((item) => {
    const haystack = `${item.supplierCode} ${item.supplierName} ${item.deliveryStatus}`.toLowerCase();
    return haystack.includes(search.toLowerCase());
  });

  const pageSize = 10;
  const totalPages = Math.max(1, Math.ceil(filteredRows.length / pageSize));
  const pagedRows = filteredRows.slice(page * pageSize, (page + 1) * pageSize);

  const summary = {
    performance: Math.round(rows.length > 0 ? rows.reduce((total, row) => total + row.performance, 0) / rows.length : 0),
    rating: Number((suppliers.length > 0 ? suppliers.reduce((total, supplier) => total + supplier.rating, 0) / suppliers.length : 0).toFixed(1)),
  };

  return (
    <div className="space-y-6">
      <PageHeader title="Supplier Management" description="Review supplier ratings, performance, and delivery reliability." />

      <div className="grid gap-4 md:grid-cols-2">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Supplier performance</p><p className="mt-2 text-3xl font-semibold">{summary.performance}%</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Average rating</p><p className="mt-2 text-3xl font-semibold">{summary.rating}</p></CardContent></Card>
      </div>

      <ResourceTable
        title="Supplier directory"
        data={pagedRows}
        columns={[
          { header: 'Supplier', cell: (item) => <div><div className="font-medium">{item.supplierName}</div><div className="text-xs text-muted-foreground">{item.supplierCode}</div></div> },
          { header: 'Rating', cell: (item) => item.rating },
          { header: 'Performance', cell: (item) => `${item.performance}%` },
          { header: 'Delivery', cell: (item) => <StatusBadge value={item.deliveryStatus} /> },
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
        emptyMessage="No suppliers were returned by the backend."
      />
    </div>
  );
}