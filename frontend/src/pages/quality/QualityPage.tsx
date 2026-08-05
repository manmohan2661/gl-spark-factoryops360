import { useMemo, useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ShieldCheck, PencilLine, Plus, Trash2, Bug, CheckCircle } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { qualityApi } from '@/api/qualityApi';
import { productionApi } from '@/api/productionApi';
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
  DefectRequest,
  DefectResponse,
  DefectSeverity,
  InspectionResult,
  QualityInspectionRequest,
  QualityInspectionResponse,
} from '@/types/quality';

type ActiveEntity = 'inspection' | 'defect';
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

function emptyInspectionForm(): QualityInspectionRequest {
  return {
    inspectorName: '',
    inspectionDate: '',
    result: 'PENDING',
    remarks: '',
    productionBatchId: 0,
  };
}

function emptyDefectForm(inspectionId: number = 0): DefectRequest {
  return {
    defectType: '',
    severity: 'LOW',
    description: '',
    reportedDate: '',
    resolved: false,
    qualityInspectionId: inspectionId,
  };
}

function normalizeSearch(value: string) {
  return value.trim().toLowerCase();
}

export function QualityPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [modal, setModal] = useState<ModalState | null>(null);
  const [deleteState, setDeleteState] = useState<DeleteState | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const [inspectionSearch, setInspectionSearch] = useState('');
  const [inspectionPage, setInspectionPage] = useState(0);
  const [inspectionForm, setInspectionForm] = useState<QualityInspectionRequest>(emptyInspectionForm());

  const [defectSearch, setDefectSearch] = useState('');
  const [defectPage, setDefectPage] = useState(0);
  const [defectForm, setDefectForm] = useState<DefectRequest>(emptyDefectForm());

  const inspectionsQuery = useQuery({ queryKey: ['quality-inspections'], queryFn: qualityApi.getQualityInspections });
  const defectsQuery = useQuery({ queryKey: ['quality-defects'], queryFn: qualityApi.getDefects });
  const batchesQuery = useQuery({ queryKey: ['production-batches'], queryFn: productionApi.getProductionBatches });

  const inspections = inspectionsQuery.data ?? [];
  const defects = defectsQuery.data ?? [];
  const batches = batchesQuery.data ?? [];

  const canManageQuality = user?.role === 'ADMIN' || user?.role === 'QUALITY_INSPECTOR';

  const inspectionMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: QualityInspectionRequest }) => {
      if (variables.id) {
        return qualityApi.updateQualityInspection(variables.id, variables.payload);
      }
      return qualityApi.createQualityInspection(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['quality-inspections'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save inspection. Check the payload and try again.'),
  });

  const defectMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: DefectRequest }) => {
      if (variables.id) {
        return qualityApi.updateDefect(variables.id, variables.payload);
      }
      return qualityApi.createDefect(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['quality-defects'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save defect. Check the payload and try again.'),
  });

  const deleteInspectionMutation = useMutation({
    mutationFn: qualityApi.deleteQualityInspection,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['quality-inspections'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the inspection.'),
  });

  const deleteDefectMutation = useMutation({
    mutationFn: qualityApi.deleteDefect,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['quality-defects'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the defect.'),
  });

  const resolveDefectMutation = useMutation({
    mutationFn: async (defect: DefectResponse) => {
      const payload: DefectRequest = {
        defectType: defect.defectType,
        severity: defect.severity,
        description: defect.description,
        reportedDate: defect.reportedDate,
        resolved: true,
        qualityInspectionId: defect.qualityInspectionId,
      };
      return qualityApi.updateDefect(defect.id, payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['quality-defects'] });
    },
  });

  if (inspectionsQuery.isLoading || defectsQuery.isLoading || batchesQuery.isLoading) {
    return <LoadingState />;
  }

  if (inspectionsQuery.isError || defectsQuery.isError || batchesQuery.isError) {
    return (
      <ErrorState
        description="Quality data could not be loaded from the API gateway."
        onRetry={() => {
          inspectionsQuery.refetch();
          defectsQuery.refetch();
          batchesQuery.refetch();
        }}
      />
    );
  }

  const defectCountByInspection = defects.reduce((counts, defect: DefectResponse) => {
    counts.set(defect.qualityInspectionId, (counts.get(defect.qualityInspectionId) ?? 0) + 1);
    return counts;
  }, new Map<number, number>());

  const displayInspections = inspections.map((inspection: QualityInspectionResponse) => ({
    ...inspection,
    defectCount: defectCountByInspection.get(inspection.id) ?? 0,
  }));

  const filteredInspections = displayInspections.filter((inspection) => {
    const haystack = `${inspection.id} ${inspection.inspectorName} ${inspection.productionBatchId} ${inspection.result} ${inspection.remarks}`.toLowerCase();
    return haystack.includes(normalizeSearch(inspectionSearch));
  });

  const filteredDefects = defects.filter((defect) => {
    const haystack = `${defect.defectType} ${defect.severity} ${defect.description} ${defect.resolved ? 'resolved' : 'unresolved'}`.toLowerCase();
    return haystack.includes(normalizeSearch(defectSearch));
  });

  const pageSize = 10;
  const inspectionTotalPages = Math.max(1, Math.ceil(filteredInspections.length / pageSize));
  const defectTotalPages = Math.max(1, Math.ceil(filteredDefects.length / pageSize));

  const pagedInspections = filteredInspections.slice(inspectionPage * pageSize, (inspectionPage + 1) * pageSize);
  const pagedDefects = filteredDefects.slice(defectPage * pageSize, (defectPage + 1) * pageSize);

  const summary = {
    totalInspections: inspections.length,
    passPercentage: inspections.length > 0 ? Math.round((inspections.filter((inspection) => inspection.result === 'PASS').length / inspections.length) * 100) : 0,
    defectCount: defects.length,
    unresolvedDefects: defects.filter((d) => !d.resolved).length,
  };

  function closeModal() {
    setModal(null);
    setSubmitError(null);
  }

  function openInspectionCreate() {
    setInspectionForm(emptyInspectionForm());
    setSubmitError(null);
    setModal({ entity: 'inspection', mode: 'create' });
  }

  function openInspectionEdit(item: QualityInspectionResponse) {
    setInspectionForm({
      inspectorName: item.inspectorName,
      inspectionDate: item.inspectionDate || '',
      result: item.result,
      remarks: item.remarks,
      productionBatchId: item.productionBatchId,
    });
    setSubmitError(null);
    setModal({ entity: 'inspection', mode: 'edit', id: item.id });
  }

  function openDefectCreate() {
    setDefectForm(emptyDefectForm(inspections[0]?.id ?? 0));
    setSubmitError(null);
    setModal({ entity: 'defect', mode: 'create' });
  }

  function openDefectEdit(item: DefectResponse) {
    setDefectForm({
      defectType: item.defectType,
      severity: item.severity,
      description: item.description,
      reportedDate: item.reportedDate || '',
      resolved: item.resolved,
      qualityInspectionId: item.qualityInspectionId,
    });
    setSubmitError(null);
    setModal({ entity: 'defect', mode: 'edit', id: item.id });
  }

  function openDeleteDialog(entity: ActiveEntity, id: number, label: string) {
    setDeleteState({ entity, id, label });
    setSubmitError(null);
  }

  async function handleInspectionSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!inspectionForm.inspectorName.trim() || inspectionForm.productionBatchId <= 0) {
      setSubmitError('Inspector name and Production Batch are required.');
      return;
    }
    const payload = { ...inspectionForm };
    if (!payload.inspectionDate) payload.inspectionDate = null;

    try {
      await inspectionMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleDefectSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!defectForm.defectType.trim() || defectForm.qualityInspectionId <= 0) {
      setSubmitError('Defect type and Quality Inspection are required.');
      return;
    }
    const payload = { ...defectForm };
    if (!payload.reportedDate) payload.reportedDate = null;

    try {
      await defectMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleDeleteConfirm() {
    if (!deleteState) return;
    try {
      if (deleteState.entity === 'inspection') {
        await deleteInspectionMutation.mutateAsync(deleteState.id);
      } else {
        await deleteDefectMutation.mutateAsync(deleteState.id);
      }
    } catch {}
  }

  const pageActions = canManageQuality ? (
    <div className="flex flex-wrap justify-end gap-2">
      <Button variant="outline" onClick={openInspectionCreate}>
        <ShieldCheck className="h-4 w-4" /> Add Inspection
      </Button>
      <Button onClick={openDefectCreate} disabled={inspections.length === 0}>
        <Bug className="h-4 w-4" /> Log Defect
      </Button>
    </div>
  ) : null;

  return (
    <div className="space-y-6">
      <PageHeader title="Quality Management" description="Inspect pass rates, log defects, and manage quality checks." actions={pageActions} />

      {submitError && !modal && (
        <Card className="border-red-200 bg-red-50/70">
          <CardContent className="p-4 text-sm text-red-700">{submitError}</CardContent>
        </Card>
      )}

      <div className="grid gap-4 md:grid-cols-4">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total inspections</p><p className="mt-2 text-3xl font-semibold">{summary.totalInspections}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Pass percentage</p><p className="mt-2 text-3xl font-semibold">{summary.passPercentage}%</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total defects</p><p className="mt-2 text-3xl font-semibold">{summary.defectCount}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Unresolved</p><p className="mt-2 text-3xl font-semibold">{summary.unresolvedDefects}</p></CardContent></Card>
      </div>

      <ResourceTable
        title="Inspection log"
        data={pagedInspections}
        columns={[
          { header: 'Inspection', cell: (item) => <div><div className="font-medium">Inspection #{item.id}</div><div className="text-xs text-muted-foreground">{item.inspectorName}</div></div> },
          { header: 'Batch', cell: (item) => `Batch #${item.productionBatchId}` },
          { header: 'Result', cell: (item) => <StatusBadge value={item.result} /> },
          { header: 'Defects', cell: (item) => item.defectCount },
          { header: 'Date', cell: (item) => formatDateTime(item.inspectionDate) },
          ...(canManageQuality
            ? [
                {
                  header: 'Actions',
                  cell: (item: any) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openInspectionEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('inspection', item.id, `Inspection #${item.id}`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={inspectionSearch}
        onSearchChange={(value) => { setInspectionPage(0); setInspectionSearch(value); }}
        page={inspectionPage}
        totalPages={inspectionTotalPages}
        onPageChange={setInspectionPage}
        emptyMessage="No inspection records were found."
      />

      <ResourceTable
        title="Defect tracker"
        data={pagedDefects}
        columns={[
          { header: 'Type', cell: (item) => <div><div className="font-medium">{item.defectType}</div><div className="text-xs text-muted-foreground">Insp. #{item.qualityInspectionId}</div></div> },
          { header: 'Severity', cell: (item) => item.severity },
          { header: 'Description', cell: (item) => item.description },
          { header: 'Status', cell: (item) => <StatusBadge value={item.resolved ? 'RESOLVED' : 'OPEN'} /> },
          { header: 'Reported', cell: (item) => formatDateTime(item.reportedDate) },
          ...(canManageQuality
            ? [
                {
                  header: 'Actions',
                  cell: (item: DefectResponse) => (
                    <div className="flex items-center gap-2">
                      {!item.resolved && (
                        <Button size="sm" variant="outline" className="text-green-600 hover:text-green-700 hover:bg-green-50 border-green-200" onClick={() => resolveDefectMutation.mutate(item)} disabled={resolveDefectMutation.isPending}>
                          <CheckCircle className="h-4 w-4" />
                        </Button>
                      )}
                      <Button size="sm" variant="outline" onClick={() => openDefectEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('defect', item.id, item.defectType)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={defectSearch}
        onSearchChange={(value) => { setDefectPage(0); setDefectSearch(value); }}
        page={defectPage}
        totalPages={defectTotalPages}
        onPageChange={setDefectPage}
        emptyMessage="No defect records were found."
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

      {modal?.entity === 'inspection' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Inspection' : 'Edit Inspection'}
          description="Log a new quality control inspection."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="inspection-form" disabled={inspectionMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="inspection-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleInspectionSubmit}>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Production Batch</label>
              <Select value={inspectionForm.productionBatchId} onChange={(e) => setInspectionForm(c => ({ ...c, productionBatchId: Number(e.target.value) }))} required>
                <option value={0} disabled>Select a batch...</option>
                {batches.map((b) => (
                  <option key={b.id} value={b.id}>{b.batchNumber} (ID: {b.id})</option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Inspector Name</label>
              <Input value={inspectionForm.inspectorName} onChange={(e) => setInspectionForm(c => ({ ...c, inspectorName: e.target.value }))} placeholder="John Doe" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Inspection Date</label>
              <Input type="datetime-local" value={inspectionForm.inspectionDate || ''} onChange={(e) => setInspectionForm(c => ({ ...c, inspectionDate: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Result</label>
              <Select value={inspectionForm.result} onChange={(e) => setInspectionForm(c => ({ ...c, result: e.target.value as InspectionResult }))}>
                <option value="PASS">PASS</option>
                <option value="FAIL">FAIL</option>
                <option value="PENDING">PENDING</option>
              </Select>
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Remarks</label>
              <Textarea value={inspectionForm.remarks} onChange={(e) => setInspectionForm(c => ({ ...c, remarks: e.target.value }))} placeholder="Inspection notes..." />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}

      {modal?.entity === 'defect' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Log Defect' : 'Edit Defect'}
          description="Record a production defect associated with an inspection."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="defect-form" disabled={defectMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="defect-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleDefectSubmit}>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Quality Inspection</label>
              <Select value={defectForm.qualityInspectionId} onChange={(e) => setDefectForm(c => ({ ...c, qualityInspectionId: Number(e.target.value) }))} required>
                <option value={0} disabled>Select an inspection...</option>
                {inspections.map((i) => (
                  <option key={i.id} value={i.id}>Insp #{i.id} - {i.inspectorName}</option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Defect Type</label>
              <Input value={defectForm.defectType} onChange={(e) => setDefectForm(c => ({ ...c, defectType: e.target.value }))} placeholder="Scratch" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Severity</label>
              <Select value={defectForm.severity} onChange={(e) => setDefectForm(c => ({ ...c, severity: e.target.value as DefectSeverity }))}>
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
                <option value="CRITICAL">CRITICAL</option>
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Reported Date</label>
              <Input type="datetime-local" value={defectForm.reportedDate || ''} onChange={(e) => setDefectForm(c => ({ ...c, reportedDate: e.target.value }))} />
            </div>
            <div className="space-y-2 flex items-center gap-2 mt-8">
              <Input id="defect-resolved" type="checkbox" checked={defectForm.resolved} onChange={(e) => setDefectForm(c => ({ ...c, resolved: e.target.checked }))} className="h-4 w-4" />
              <label htmlFor="defect-resolved" className="text-sm font-medium">Resolved</label>
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Description</label>
              <Textarea value={defectForm.description} onChange={(e) => setDefectForm(c => ({ ...c, description: e.target.value }))} placeholder="Detailed description..." />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}
    </div>
  );
}