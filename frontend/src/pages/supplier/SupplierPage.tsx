import { useMemo, useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Truck, PencilLine, Plus, Trash2, ClipboardCheck } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { supplierApi } from '@/api/supplierApi';
import { ErrorState } from '@/components/common/ErrorState';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { LoadingState } from '@/components/common/LoadingState';
import { Modal } from '@/components/common/Modal';
import { PageHeader } from '@/components/common/PageHeader';
import { ResourceTable } from '@/components/tables/ResourceTable';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Select } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { formatDateTime } from '@/utils/date';
import type {
  SupplierPerformanceRequest,
  SupplierPerformanceResponse,
  SupplierRequest,
  SupplierResponse,
  SupplierStatus,
} from '@/types/supplier';

type ActiveEntity = 'supplier' | 'performance';
type ModalMode = 'create' | 'edit';

interface ModalState {
  entity: ActiveEntity;
  mode: ModalMode;
  id?: number;
}

interface DeleteState {
  entity: ActiveEntity;
  id: number;
  label: string;
}

function emptySupplierForm(): SupplierRequest {
  return {
    code: '',
    name: '',
    contactPerson: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    country: '',
    status: 'ACTIVE',
    rating: 0,
  };
}

function emptyPerformanceForm(supplierId: number = 0): SupplierPerformanceRequest {
  return {
    evaluationPeriod: '',
    onTimeDeliveryRate: 0,
    qualityScore: 0,
    defectRate: 0,
    remarks: '',
    evaluatedAt: '',
    supplierId,
  };
}

function normalizeSearch(value: string) {
  return value.trim().toLowerCase();
}

function deriveDeliveryStatus(onTimeDeliveryRate: number) {
  if (onTimeDeliveryRate >= 90) return 'ON_TIME' as const;
  if (onTimeDeliveryRate >= 75) return 'AT_RISK' as const;
  return 'DELAYED' as const;
}

export function SupplierPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [modal, setModal] = useState<ModalState | null>(null);
  const [deleteState, setDeleteState] = useState<DeleteState | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const [supplierSearch, setSupplierSearch] = useState('');
  const [supplierPage, setSupplierPage] = useState(0);
  const [supplierForm, setSupplierForm] = useState<SupplierRequest>(emptySupplierForm());

  const [performanceSearch, setPerformanceSearch] = useState('');
  const [performancePage, setPerformancePage] = useState(0);
  const [performanceForm, setPerformanceForm] = useState<SupplierPerformanceRequest>(emptyPerformanceForm());

  const suppliersQuery = useQuery({ queryKey: ['suppliers'], queryFn: supplierApi.getSuppliers });
  const performancesQuery = useQuery({ queryKey: ['supplier-performances'], queryFn: supplierApi.getSupplierPerformances });

  const suppliers = suppliersQuery.data ?? [];
  const performances = performancesQuery.data ?? [];

  const canManageSuppliers = user?.role === 'ADMIN' || user?.role === 'SUPPLIER_MANAGER';

  const supplierById = useMemo(() => new Map<number, SupplierResponse>(suppliers.map((s) => [s.id, s])), [suppliers]);

  const latestPerformanceBySupplier = performances.reduce((map, performance: SupplierPerformanceResponse) => {
    const existing = map.get(performance.supplierId);
    if (!existing || new Date(performance.evaluatedAt).getTime() > new Date(existing.evaluatedAt).getTime()) {
      map.set(performance.supplierId, performance);
    }
    return map;
  }, new Map<number, SupplierPerformanceResponse>());

  const supplierMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: SupplierRequest }) => {
      if (variables.id) {
        return supplierApi.updateSupplier(variables.id, variables.payload);
      }
      return supplierApi.createSupplier(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['suppliers'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save supplier. Check the payload and try again.'),
  });

  const performanceMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: SupplierPerformanceRequest }) => {
      if (variables.id) {
        return supplierApi.updateSupplierPerformance(variables.id, variables.payload);
      }
      return supplierApi.createSupplierPerformance(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['supplier-performances'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save performance evaluation. Check the payload and try again.'),
  });

  const deleteSupplierMutation = useMutation({
    mutationFn: supplierApi.deleteSupplier,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['suppliers'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the supplier.'),
  });

  const deletePerformanceMutation = useMutation({
    mutationFn: supplierApi.deleteSupplierPerformance,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['supplier-performances'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the performance record.'),
  });

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

  const supplierRows = suppliers.map((supplier) => {
    const perf = latestPerformanceBySupplier.get(supplier.id);
    const deliveryRate = perf?.onTimeDeliveryRate ?? 0;
    return {
      ...supplier,
      performanceScore: deliveryRate,
      deliveryStatus: deriveDeliveryStatus(deliveryRate),
    };
  });

  const filteredSuppliers = supplierRows.filter((item) => {
    const haystack = `${item.code} ${item.name} ${item.status} ${item.country}`.toLowerCase();
    return haystack.includes(normalizeSearch(supplierSearch));
  });

  const filteredPerformances = performances.filter((perf) => {
    const s = supplierById.get(perf.supplierId);
    const haystack = `${s?.name} ${perf.evaluationPeriod} ${perf.remarks}`.toLowerCase();
    return haystack.includes(normalizeSearch(performanceSearch));
  });

  const pageSize = 10;
  const supplierTotalPages = Math.max(1, Math.ceil(filteredSuppliers.length / pageSize));
  const performanceTotalPages = Math.max(1, Math.ceil(filteredPerformances.length / pageSize));

  const pagedSuppliers = filteredSuppliers.slice(supplierPage * pageSize, (supplierPage + 1) * pageSize);
  const pagedPerformances = filteredPerformances.slice(performancePage * pageSize, (performancePage + 1) * pageSize);

  const summary = {
    totalSuppliers: suppliers.length,
    activeSuppliers: suppliers.filter((s) => s.status === 'ACTIVE').length,
    performance: Math.round(supplierRows.length > 0 ? supplierRows.reduce((total, row) => total + row.performanceScore, 0) / supplierRows.length : 0),
    rating: Number((suppliers.length > 0 ? suppliers.reduce((total, supplier) => total + supplier.rating, 0) / suppliers.length : 0).toFixed(1)),
  };

  function closeModal() {
    setModal(null);
    setSubmitError(null);
  }

  function openSupplierCreate() {
    setSupplierForm(emptySupplierForm());
    setSubmitError(null);
    setModal({ entity: 'supplier', mode: 'create' });
  }

  function openSupplierEdit(item: SupplierResponse) {
    setSupplierForm({
      code: item.code,
      name: item.name,
      contactPerson: item.contactPerson || '',
      email: item.email || '',
      phone: item.phone || '',
      address: item.address || '',
      city: item.city || '',
      country: item.country || '',
      status: item.status,
      rating: item.rating,
    });
    setSubmitError(null);
    setModal({ entity: 'supplier', mode: 'edit', id: item.id });
  }

  function openPerformanceCreate() {
    setPerformanceForm(emptyPerformanceForm(suppliers[0]?.id ?? 0));
    setSubmitError(null);
    setModal({ entity: 'performance', mode: 'create' });
  }

  function openPerformanceEdit(item: SupplierPerformanceResponse) {
    setPerformanceForm({
      evaluationPeriod: item.evaluationPeriod,
      onTimeDeliveryRate: item.onTimeDeliveryRate,
      qualityScore: item.qualityScore,
      defectRate: item.defectRate,
      remarks: item.remarks || '',
      evaluatedAt: item.evaluatedAt || '',
      supplierId: item.supplierId,
    });
    setSubmitError(null);
    setModal({ entity: 'performance', mode: 'edit', id: item.id });
  }

  function openDeleteDialog(entity: ActiveEntity, id: number, label: string) {
    setDeleteState({ entity, id, label });
    setSubmitError(null);
  }

  async function handleSupplierSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!supplierForm.code.trim() || !supplierForm.name.trim()) {
      setSubmitError('Supplier code and name are required.');
      return;
    }
    try {
      await supplierMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload: supplierForm });
    } catch {}
  }

  async function handlePerformanceSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (performanceForm.supplierId <= 0 || !performanceForm.evaluationPeriod.trim()) {
      setSubmitError('Supplier and evaluation period are required.');
      return;
    }
    const payload = { ...performanceForm };
    if (!payload.evaluatedAt) payload.evaluatedAt = null;

    try {
      await performanceMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleDeleteConfirm() {
    if (!deleteState) return;
    try {
      if (deleteState.entity === 'supplier') {
        await deleteSupplierMutation.mutateAsync(deleteState.id);
      } else {
        await deletePerformanceMutation.mutateAsync(deleteState.id);
      }
    } catch {}
  }

  const pageActions = canManageSuppliers ? (
    <div className="flex flex-wrap justify-end gap-2">
      <Button variant="outline" onClick={openSupplierCreate}>
        <Plus className="h-4 w-4" /> Add Supplier
      </Button>
      <Button onClick={openPerformanceCreate} disabled={suppliers.length === 0}>
        <ClipboardCheck className="h-4 w-4" /> Evaluate Performance
      </Button>
    </div>
  ) : null;

  return (
    <div className="space-y-6">
      <PageHeader title="Supplier Management" description="Review supplier ratings, manage details, and evaluate performance." actions={pageActions} />

      {submitError && !modal && (
        <Card className="border-red-200 bg-red-50/70">
          <CardContent className="p-4 text-sm text-red-700">{submitError}</CardContent>
        </Card>
      )}

      <div className="grid gap-4 md:grid-cols-4">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total suppliers</p><p className="mt-2 text-3xl font-semibold">{summary.totalSuppliers}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Active suppliers</p><p className="mt-2 text-3xl font-semibold">{summary.activeSuppliers}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Avg performance</p><p className="mt-2 text-3xl font-semibold">{summary.performance}%</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Average rating</p><p className="mt-2 text-3xl font-semibold">{summary.rating}</p></CardContent></Card>
      </div>

      <ResourceTable
        title="Supplier directory"
        data={pagedSuppliers}
        columns={[
          { header: 'Supplier', cell: (item) => <div><div className="font-medium">{item.name}</div><div className="text-xs text-muted-foreground">{item.code}</div></div> },
          { header: 'Status', cell: (item) => <StatusBadge value={item.status} /> },
          { header: 'Rating', cell: (item) => item.rating },
          { header: 'Delivery Perf.', cell: (item) => `${item.performanceScore}%` },
          { header: 'Delivery Status', cell: (item) => <StatusBadge value={item.deliveryStatus} /> },
          ...(canManageSuppliers
            ? [
                {
                  header: 'Actions',
                  cell: (item: any) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openSupplierEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('supplier', item.id, item.name)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={supplierSearch}
        onSearchChange={(value) => { setSupplierPage(0); setSupplierSearch(value); }}
        page={supplierPage}
        totalPages={supplierTotalPages}
        onPageChange={setSupplierPage}
        emptyMessage="No suppliers found."
      />

      <ResourceTable
        title="Performance evaluations"
        data={pagedPerformances}
        columns={[
          { header: 'Supplier', cell: (item) => <div><div className="font-medium">{supplierById.get(item.supplierId)?.name || 'Unknown'}</div><div className="text-xs text-muted-foreground">{item.evaluationPeriod}</div></div> },
          { header: 'On-Time Del.', cell: (item) => `${item.onTimeDeliveryRate}%` },
          { header: 'Quality Score', cell: (item) => `${item.qualityScore}%` },
          { header: 'Defect Rate', cell: (item) => `${item.defectRate}%` },
          { header: 'Evaluated', cell: (item) => formatDateTime(item.evaluatedAt) },
          ...(canManageSuppliers
            ? [
                {
                  header: 'Actions',
                  cell: (item: SupplierPerformanceResponse) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openPerformanceEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('performance', item.id, `Evaluation for ${supplierById.get(item.supplierId)?.name}`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={performanceSearch}
        onSearchChange={(value) => { setPerformancePage(0); setPerformanceSearch(value); }}
        page={performancePage}
        totalPages={performanceTotalPages}
        onPageChange={setPerformancePage}
        emptyMessage="No performance evaluations found."
      />

      {deleteState && (
        <ConfirmDialog
          open
          title="Confirm Deletion"
          description={`Are you sure you want to delete ${deleteState.label}? This action cannot be undone.`}
          confirmLabel="Delete"
          onClose={() => {
            setDeleteState(null);
            setSubmitError(null);
          }}
          onConfirm={handleDeleteConfirm}
          confirmTone="destructive"
        />
      )}

      {modal?.entity === 'supplier' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Supplier' : 'Edit Supplier'}
          description="Manage supplier information."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="supplier-form" disabled={supplierMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="supplier-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleSupplierSubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Code</label>
              <Input value={supplierForm.code} onChange={(e) => setSupplierForm(c => ({ ...c, code: e.target.value }))} placeholder="SUP-001" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Name</label>
              <Input value={supplierForm.name} onChange={(e) => setSupplierForm(c => ({ ...c, name: e.target.value }))} placeholder="Acme Corp" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Contact Person</label>
              <Input value={supplierForm.contactPerson} onChange={(e) => setSupplierForm(c => ({ ...c, contactPerson: e.target.value }))} placeholder="Jane Doe" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Email</label>
              <Input type="email" value={supplierForm.email} onChange={(e) => setSupplierForm(c => ({ ...c, email: e.target.value }))} placeholder="jane@acme.com" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Phone</label>
              <Input value={supplierForm.phone} onChange={(e) => setSupplierForm(c => ({ ...c, phone: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Status</label>
              <Select value={supplierForm.status} onChange={(e) => setSupplierForm(c => ({ ...c, status: e.target.value as SupplierStatus }))}>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="BLACKLISTED">BLACKLISTED</option>
              </Select>
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Address</label>
              <Input value={supplierForm.address} onChange={(e) => setSupplierForm(c => ({ ...c, address: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">City</label>
              <Input value={supplierForm.city} onChange={(e) => setSupplierForm(c => ({ ...c, city: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Country</label>
              <Input value={supplierForm.country} onChange={(e) => setSupplierForm(c => ({ ...c, country: e.target.value }))} />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Rating (0 - 5)</label>
              <Input type="number" min={0} max={5} step="0.1" value={supplierForm.rating} onChange={(e) => setSupplierForm(c => ({ ...c, rating: Number(e.target.value) }))} />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}

      {modal?.entity === 'performance' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Evaluation' : 'Edit Evaluation'}
          description="Record supplier performance metrics."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="performance-form" disabled={performanceMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="performance-form" className="grid gap-4 md:grid-cols-2" onSubmit={handlePerformanceSubmit}>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Supplier</label>
              <Select value={performanceForm.supplierId} onChange={(e) => setPerformanceForm(c => ({ ...c, supplierId: Number(e.target.value) }))} required>
                <option value={0} disabled>Select a supplier...</option>
                {suppliers.map((s) => (
                  <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                ))}
              </Select>
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Evaluation Period (e.g. Q1 2026)</label>
              <Input value={performanceForm.evaluationPeriod} onChange={(e) => setPerformanceForm(c => ({ ...c, evaluationPeriod: e.target.value }))} required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">On-Time Delivery Rate (%)</label>
              <Input type="number" min={0} max={100} value={performanceForm.onTimeDeliveryRate} onChange={(e) => setPerformanceForm(c => ({ ...c, onTimeDeliveryRate: Number(e.target.value) }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Quality Score (%)</label>
              <Input type="number" min={0} max={100} value={performanceForm.qualityScore} onChange={(e) => setPerformanceForm(c => ({ ...c, qualityScore: Number(e.target.value) }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Defect Rate (%)</label>
              <Input type="number" min={0} max={100} value={performanceForm.defectRate} onChange={(e) => setPerformanceForm(c => ({ ...c, defectRate: Number(e.target.value) }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Evaluated At</label>
              <Input type="datetime-local" value={performanceForm.evaluatedAt || ''} onChange={(e) => setPerformanceForm(c => ({ ...c, evaluatedAt: e.target.value }))} />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Remarks</label>
              <Textarea value={performanceForm.remarks} onChange={(e) => setPerformanceForm(c => ({ ...c, remarks: e.target.value }))} />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}
    </div>
  );
}