import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { BellRing } from 'lucide-react';
import { alertApi } from '@/api/alertApi';
import { EmptyState } from '@/components/common/EmptyState';
import { ErrorState } from '@/components/common/ErrorState';
import { LoadingState } from '@/components/common/LoadingState';
import { PageHeader } from '@/components/common/PageHeader';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Select } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { formatDateTime } from '@/utils/date';
import type { AlertResponse } from '@/types/alerts';

export function AlertCenterPage() {
  const [search, setSearch] = useState('');
  const [severity, setSeverity] = useState('ALL');

  const alertsQuery = useQuery({ queryKey: ['alerts'], queryFn: alertApi.getAlerts });

  if (alertsQuery.isLoading) {
    return <LoadingState />;
  }

  if (alertsQuery.isError) {
    return <ErrorState description="The alert feed could not be loaded from the API gateway." onRetry={() => alertsQuery.refetch()} />;
  }

  const alerts = (alertsQuery.data ?? []).filter((alert: AlertResponse) => {
    const matchesSearch = `${alert.title} ${alert.message} ${alert.sourceService}`.toLowerCase().includes(search.toLowerCase());
    const matchesSeverity = severity === 'ALL' || alert.severity === severity;
    return matchesSearch && matchesSeverity;
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Alert Center"
        description="Monitor critical, warning, and informational operational events in a single queue."
        actions={<Button onClick={() => alertsQuery.refetch()}>Refresh</Button>}
      />

      <Card>
        <CardContent className="flex flex-col gap-3 p-6 lg:flex-row">
          <div className="flex-1">
            <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search alerts" />
          </div>
          <Select value={severity} onChange={(event) => setSeverity(event.target.value)} className="max-w-xs">
            <option value="ALL">All severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </Select>
        </CardContent>
      </Card>

      {alerts.length === 0 ? (
        <EmptyState title="No alerts match the current filters" description="Adjust the search term or alert type to review the active feed." />
      ) : (
        <Card>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                    <TableHead>Severity</TableHead>
                  <TableHead>Title</TableHead>
                    <TableHead>Message</TableHead>
                  <TableHead>Timestamp</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {alerts.map((alert) => (
                  <TableRow key={alert.id}>
                    <TableCell>
                        <StatusBadge value={alert.severity} />
                    </TableCell>
                    <TableCell className="font-medium">{alert.title}</TableCell>
                      <TableCell className="text-muted-foreground">{alert.message}</TableCell>
                      <TableCell>{formatDateTime(alert.triggeredAt)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}
    </div>
  );
}