import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { qualityApi } from '@/api/qualityApi';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { ResourceTable } from '@/components/tables/ResourceTable';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Card, CardContent } from '@/components/ui/card';
import { formatDateTime } from '@/utils/date';
import type { DefectResponse, QualityInspectionResponse } from '@/types/quality';

export function QualityPage() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const inspectionsQuery = useQuery({ queryKey: ['quality-inspections'], queryFn: qualityApi.getQualityInspections });
  const defectsQuery = useQuery({ queryKey: ['defects'], queryFn: qualityApi.getDefects });

  if (inspectionsQuery.isLoading || defectsQuery.isLoading) {
    return <LoadingState />;
  }

  if (inspectionsQuery.isError || defectsQuery.isError) {
    return (
      <ErrorState
        description="Quality data could not be loaded from the API gateway."
        onRetry={() => {
          inspectionsQuery.refetch();
          defectsQuery.refetch();
        }}
      />
    );
  }

  const inspections = inspectionsQuery.data ?? [];
  const defects = defectsQuery.data ?? [];

  const defectCountByInspection = defects.reduce((counts, defect: DefectResponse) => {
    counts.set(defect.qualityInspectionId, (counts.get(defect.qualityInspectionId) ?? 0) + 1);
    return counts;
  }, new Map<number, number>());

  const displayRows = inspections.map((inspection: QualityInspectionResponse) => ({
    ...inspection,
    defectCount: defectCountByInspection.get(inspection.id) ?? 0,
  }));

  const filteredRows = displayRows.filter((inspection) => {
    const haystack = `${inspection.id} ${inspection.inspectorName} ${inspection.productionBatchId} ${inspection.result} ${inspection.remarks}`.toLowerCase();
    return haystack.includes(search.toLowerCase());
  });

  const pageSize = 10;
  const totalPages = Math.max(1, Math.ceil(filteredRows.length / pageSize));
  const pagedRows = filteredRows.slice(page * pageSize, (page + 1) * pageSize);

  const summary = {
    totalInspections: inspections.length,
    passPercentage: inspections.length > 0 ? Math.round((inspections.filter((inspection) => inspection.result === 'PASS').length / inspections.length) * 100) : 0,
    defectCount: defects.length,
  };

  return (
    <div className="space-y-6">
      <PageHeader title="Quality Management" description="Inspect pass rates, defect counts, and line-level quality trends." />

      <div className="grid gap-4 md:grid-cols-3">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total inspections</p><p className="mt-2 text-3xl font-semibold">{summary.totalInspections}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Pass percentage</p><p className="mt-2 text-3xl font-semibold">{summary.passPercentage}%</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Defect count</p><p className="mt-2 text-3xl font-semibold">{summary.defectCount}</p></CardContent></Card>
      </div>

      <ResourceTable
        title="Inspection log"
        data={pagedRows}
        columns={[
          { header: 'Inspection', cell: (item) => <div><div className="font-medium">Inspection #{item.id}</div><div className="text-xs text-muted-foreground">{item.inspectorName}</div></div> },
          { header: 'Batch', cell: (item) => item.productionBatchId },
          { header: 'Result', cell: (item) => <StatusBadge value={item.result} /> },
          { header: 'Defects', cell: (item) => item.defectCount },
          { header: 'Inspected at', cell: (item) => formatDateTime(item.inspectionDate) },
        ]}
        search={search}
        onSearchChange={(value) => {
          setPage(0);
          setSearch(value);
        }}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
        emptyMessage="No inspection records were returned by the backend."
      />
    </div>
  );
}