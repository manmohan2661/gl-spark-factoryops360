import { useMemo, useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ClipboardList, PencilLine, Plus, Trash2, Settings, Wrench } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
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
  MachineMaintenanceRequest,
  MachineMaintenanceResponse,
  MachineRequest,
  MachineResponse,
  MachineStatus,
  MaintenanceStatus,
  MaintenanceType,
  ProductionOrderRequest,
  ProductionOrderResponse,
  ProductionOrderStatus,
} from '@/types/production';

type ActiveEntity = 'order' | 'machine' | 'maintenance';
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

function emptyOrderForm(): ProductionOrderRequest {
  return {
    orderNumber: '',
    productName: '',
    quantityOrdered: 0,
    quantityProduced: 0,
    status: 'PLANNED',
    priority: 1,
    startDate: '',
    endDate: '',
  };
}

function emptyMachineForm(): MachineRequest {
  return {
    machineCode: '',
    name: '',
    type: '',
    status: 'OPERATIONAL',
    location: '',
    installationDate: '',
  };
}

function emptyMaintenanceForm(machineId: number = 0): MachineMaintenanceRequest {
  return {
    maintenanceType: 'PREVENTIVE',
    status: 'SCHEDULED',
    scheduledDate: '',
    completedDate: '',
    remarks: '',
    machineId,
  };
}

function normalizeSearch(value: string) {
  return value.trim().toLowerCase();
}

export function ProductionPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [modal, setModal] = useState<ModalState | null>(null);
  const [deleteState, setDeleteState] = useState<DeleteState | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const [orderSearch, setOrderSearch] = useState('');
  const [orderPage, setOrderPage] = useState(0);
  const [orderForm, setOrderForm] = useState<ProductionOrderRequest>(emptyOrderForm());

  const [machineSearch, setMachineSearch] = useState('');
  const [machinePage, setMachinePage] = useState(0);
  const [machineForm, setMachineForm] = useState<MachineRequest>(emptyMachineForm());

  const [maintenanceSearch, setMaintenanceSearch] = useState('');
  const [maintenancePage, setMaintenancePage] = useState(0);
  const [maintenanceForm, setMaintenanceForm] = useState<MachineMaintenanceRequest>(emptyMaintenanceForm());

  const ordersQuery = useQuery({ queryKey: ['production-orders'], queryFn: productionApi.getProductionOrders });
  const machinesQuery = useQuery({ queryKey: ['production-machines'], queryFn: productionApi.getMachines });
  const maintenancesQuery = useQuery({ queryKey: ['production-maintenances'], queryFn: productionApi.getMachineMaintenances });

  const orders = ordersQuery.data ?? [];
  const machines = machinesQuery.data ?? [];
  const maintenances = maintenancesQuery.data ?? [];

  const canManageProduction = user?.role === 'ADMIN' || user?.role === 'PRODUCTION_MANAGER';

  const machineById = useMemo(() => new Map<number, MachineResponse>(machines.map((m) => [m.id, m])), [machines]);

  const orderMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: ProductionOrderRequest }) => {
      if (variables.id) {
        return productionApi.updateProductionOrder(variables.id, variables.payload);
      }
      return productionApi.createProductionOrder(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-orders'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save order. Check the payload and try again.'),
  });

  const machineMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: MachineRequest }) => {
      if (variables.id) {
        return productionApi.updateMachine(variables.id, variables.payload);
      }
      return productionApi.createMachine(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-machines'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save machine. Check the payload and try again.'),
  });

  const maintenanceMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: MachineMaintenanceRequest }) => {
      if (variables.id) {
        return productionApi.updateMachineMaintenance(variables.id, variables.payload);
      }
      return productionApi.createMachineMaintenance(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-maintenances'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save maintenance record. Check the payload and try again.'),
  });

  const deleteOrderMutation = useMutation({
    mutationFn: productionApi.deleteProductionOrder,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-orders'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the order.'),
  });

  const deleteMachineMutation = useMutation({
    mutationFn: productionApi.deleteMachine,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-machines'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the machine.'),
  });

  const deleteMaintenanceMutation = useMutation({
    mutationFn: productionApi.deleteMachineMaintenance,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['production-maintenances'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the maintenance record.'),
  });

  if (ordersQuery.isLoading || machinesQuery.isLoading || maintenancesQuery.isLoading) {
    return <LoadingState />;
  }

  if (ordersQuery.isError || machinesQuery.isError || maintenancesQuery.isError) {
    return (
      <ErrorState
        description="Production data could not be loaded from the API gateway."
        onRetry={() => {
          ordersQuery.refetch();
          machinesQuery.refetch();
          maintenancesQuery.refetch();
        }}
      />
    );
  }

  const filteredOrders = orders.filter((order) => {
    const haystack = `${order.orderNumber} ${order.productName} ${order.status}`.toLowerCase();
    return haystack.includes(normalizeSearch(orderSearch));
  });

  const filteredMachines = machines.filter((machine) => {
    const haystack = `${machine.machineCode} ${machine.name} ${machine.type} ${machine.status} ${machine.location}`.toLowerCase();
    return haystack.includes(normalizeSearch(machineSearch));
  });

  const filteredMaintenances = maintenances.filter((maint) => {
    const machine = machineById.get(maint.machineId);
    const haystack = `${machine?.name} ${maint.maintenanceType} ${maint.status} ${maint.remarks}`.toLowerCase();
    return haystack.includes(normalizeSearch(maintenanceSearch));
  });

  const pageSize = 10;
  const orderTotalPages = Math.max(1, Math.ceil(filteredOrders.length / pageSize));
  const machineTotalPages = Math.max(1, Math.ceil(filteredMachines.length / pageSize));
  const maintenanceTotalPages = Math.max(1, Math.ceil(filteredMaintenances.length / pageSize));

  const pagedOrders = filteredOrders.slice(orderPage * pageSize, (orderPage + 1) * pageSize);
  const pagedMachines = filteredMachines.slice(machinePage * pageSize, (machinePage + 1) * pageSize);
  const pagedMaintenances = filteredMaintenances.slice(maintenancePage * pageSize, (maintenancePage + 1) * pageSize);

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

  function closeModal() {
    setModal(null);
    setSubmitError(null);
  }

  function openOrderCreate() {
    setOrderForm(emptyOrderForm());
    setSubmitError(null);
    setModal({ entity: 'order', mode: 'create' });
  }

  function openOrderEdit(item: ProductionOrderResponse) {
    setOrderForm({
      orderNumber: item.orderNumber,
      productName: item.productName,
      quantityOrdered: item.quantityOrdered,
      quantityProduced: item.quantityProduced,
      status: item.status,
      priority: item.priority,
      startDate: item.startDate || '',
      endDate: item.endDate || '',
    });
    setSubmitError(null);
    setModal({ entity: 'order', mode: 'edit', id: item.id });
  }

  function openMachineCreate() {
    setMachineForm(emptyMachineForm());
    setSubmitError(null);
    setModal({ entity: 'machine', mode: 'create' });
  }

  function openMachineEdit(item: MachineResponse) {
    setMachineForm({
      machineCode: item.machineCode,
      name: item.name,
      type: item.type,
      status: item.status,
      location: item.location,
      installationDate: item.installationDate || '',
    });
    setSubmitError(null);
    setModal({ entity: 'machine', mode: 'edit', id: item.id });
  }

  function openMaintenanceCreate() {
    setMaintenanceForm(emptyMaintenanceForm(machines[0]?.id ?? 0));
    setSubmitError(null);
    setModal({ entity: 'maintenance', mode: 'create' });
  }

  function openMaintenanceEdit(item: MachineMaintenanceResponse) {
    setMaintenanceForm({
      maintenanceType: item.maintenanceType,
      status: item.status,
      scheduledDate: item.scheduledDate || '',
      completedDate: item.completedDate || '',
      remarks: item.remarks,
      machineId: item.machineId,
    });
    setSubmitError(null);
    setModal({ entity: 'maintenance', mode: 'edit', id: item.id });
  }

  function openDeleteDialog(entity: ActiveEntity, id: number, label: string) {
    setDeleteState({ entity, id, label });
    setSubmitError(null);
  }

  async function handleOrderSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!orderForm.orderNumber.trim() || !orderForm.productName.trim()) {
      setSubmitError('Order number and product name are required.');
      return;
    }
    const payload = { ...orderForm };
    if (!payload.startDate) payload.startDate = null;
    if (!payload.endDate) payload.endDate = null;

    try {
      await orderMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleMachineSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!machineForm.machineCode.trim() || !machineForm.name.trim()) {
      setSubmitError('Machine code and name are required.');
      return;
    }
    const payload = { ...machineForm };
    if (!payload.installationDate) payload.installationDate = null;

    try {
      await machineMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleMaintenanceSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (maintenanceForm.machineId <= 0) {
      setSubmitError('Please select a machine.');
      return;
    }
    const payload = { ...maintenanceForm };
    if (!payload.scheduledDate) payload.scheduledDate = null;
    if (!payload.completedDate) payload.completedDate = null;

    try {
      await maintenanceMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload });
    } catch {}
  }

  async function handleDeleteConfirm() {
    if (!deleteState) return;
    try {
      if (deleteState.entity === 'order') {
        await deleteOrderMutation.mutateAsync(deleteState.id);
      } else if (deleteState.entity === 'machine') {
        await deleteMachineMutation.mutateAsync(deleteState.id);
      } else {
        await deleteMaintenanceMutation.mutateAsync(deleteState.id);
      }
    } catch {}
  }

  const pageActions = canManageProduction ? (
    <div className="flex flex-wrap justify-end gap-2">
      <Button variant="outline" onClick={openOrderCreate}>
        <Plus className="h-4 w-4" /> Add Order
      </Button>
      <Button variant="outline" onClick={openMachineCreate}>
        <Settings className="h-4 w-4" /> Add Machine
      </Button>
      <Button onClick={openMaintenanceCreate} disabled={machines.length === 0}>
        <Wrench className="h-4 w-4" /> Add Maintenance
      </Button>
    </div>
  ) : null;

  return (
    <div className="space-y-6">
      <PageHeader title="Production Management" description="Manage production orders, equipment, and maintenance schedules." actions={pageActions} />

      {submitError && !modal && (
        <Card className="border-red-200 bg-red-50/70">
          <CardContent className="p-4 text-sm text-red-700">{submitError}</CardContent>
        </Card>
      )}

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
          ...(canManageProduction
            ? [
                {
                  header: 'Actions',
                  cell: (item: ProductionOrderResponse) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openOrderEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('order', item.id, item.orderNumber)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={orderSearch}
        onSearchChange={(value) => { setOrderPage(0); setOrderSearch(value); }}
        page={orderPage}
        totalPages={orderTotalPages}
        onPageChange={setOrderPage}
        emptyMessage="No production orders were found."
      />

      <ResourceTable
        title="Machines"
        data={pagedMachines}
        columns={[
          { header: 'Machine', cell: (item) => <div><div className="font-medium">{item.name}</div><div className="text-xs text-muted-foreground">{item.machineCode}</div></div> },
          { header: 'Type', cell: (item) => item.type },
          { header: 'Location', cell: (item) => item.location },
          { header: 'Status', cell: (item) => <StatusBadge value={item.status} /> },
          { header: 'Updated', cell: (item) => formatDateTime(item.updatedAt) },
          ...(canManageProduction
            ? [
                {
                  header: 'Actions',
                  cell: (item: MachineResponse) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openMachineEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('machine', item.id, item.name)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={machineSearch}
        onSearchChange={(value) => { setMachinePage(0); setMachineSearch(value); }}
        page={machinePage}
        totalPages={machineTotalPages}
        onPageChange={setMachinePage}
        emptyMessage="No machines were found."
      />

      <ResourceTable
        title="Maintenance Schedule"
        data={pagedMaintenances}
        columns={[
          { header: 'Machine', cell: (item) => {
              const m = machineById.get(item.machineId);
              return <div><div className="font-medium">{m?.name || 'Unknown'}</div><div className="text-xs text-muted-foreground">{m?.machineCode}</div></div>;
            } 
          },
          { header: 'Type', cell: (item) => item.maintenanceType },
          { header: 'Status', cell: (item) => <StatusBadge value={item.status} /> },
          { header: 'Scheduled', cell: (item) => item.scheduledDate || '-' },
          { header: 'Completed', cell: (item) => item.completedDate || '-' },
          ...(canManageProduction
            ? [
                {
                  header: 'Actions',
                  cell: (item: MachineMaintenanceResponse) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openMaintenanceEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('maintenance', item.id, `Maintenance on ${machineById.get(item.machineId)?.name}`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={maintenanceSearch}
        onSearchChange={(value) => { setMaintenancePage(0); setMaintenanceSearch(value); }}
        page={maintenancePage}
        totalPages={maintenanceTotalPages}
        onPageChange={setMaintenancePage}
        emptyMessage="No maintenance records were found."
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

      {modal?.entity === 'order' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Order' : 'Edit Order'}
          description="Manage production order details."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="order-form" disabled={orderMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="order-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleOrderSubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Order Number</label>
              <Input value={orderForm.orderNumber} onChange={(e) => setOrderForm(c => ({ ...c, orderNumber: e.target.value }))} placeholder="ORD-001" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Product Name</label>
              <Input value={orderForm.productName} onChange={(e) => setOrderForm(c => ({ ...c, productName: e.target.value }))} placeholder="Product" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Quantity Ordered</label>
              <Input type="number" min={0} value={orderForm.quantityOrdered} onChange={(e) => setOrderForm(c => ({ ...c, quantityOrdered: Number(e.target.value) }))} required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Quantity Produced</label>
              <Input type="number" min={0} value={orderForm.quantityProduced} onChange={(e) => setOrderForm(c => ({ ...c, quantityProduced: Number(e.target.value) }))} required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Status</label>
              <Select value={orderForm.status} onChange={(e) => setOrderForm(c => ({ ...c, status: e.target.value as ProductionOrderStatus }))}>
                <option value="PLANNED">PLANNED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
                <option value="ON_HOLD">ON_HOLD</option>
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Priority</label>
              <Input type="number" min={1} max={5} value={orderForm.priority} onChange={(e) => setOrderForm(c => ({ ...c, priority: Number(e.target.value) }))} required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Start Date</label>
              <Input type="date" value={orderForm.startDate || ''} onChange={(e) => setOrderForm(c => ({ ...c, startDate: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">End Date</label>
              <Input type="date" value={orderForm.endDate || ''} onChange={(e) => setOrderForm(c => ({ ...c, endDate: e.target.value }))} />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}

      {modal?.entity === 'machine' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Machine' : 'Edit Machine'}
          description="Manage manufacturing equipment."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="machine-form" disabled={machineMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="machine-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleMachineSubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Machine Code</label>
              <Input value={machineForm.machineCode} onChange={(e) => setMachineForm(c => ({ ...c, machineCode: e.target.value }))} placeholder="MAC-001" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Name</label>
              <Input value={machineForm.name} onChange={(e) => setMachineForm(c => ({ ...c, name: e.target.value }))} placeholder="CNC Lathe" required />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Type</label>
              <Input value={machineForm.type} onChange={(e) => setMachineForm(c => ({ ...c, type: e.target.value }))} placeholder="Milling" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Status</label>
              <Select value={machineForm.status} onChange={(e) => setMachineForm(c => ({ ...c, status: e.target.value as MachineStatus }))}>
                <option value="OPERATIONAL">OPERATIONAL</option>
                <option value="UNDER_MAINTENANCE">UNDER_MAINTENANCE</option>
                <option value="BREAKDOWN">BREAKDOWN</option>
                <option value="DECOMMISSIONED">DECOMMISSIONED</option>
              </Select>
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Location</label>
              <Input value={machineForm.location} onChange={(e) => setMachineForm(c => ({ ...c, location: e.target.value }))} placeholder="Line 1" />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Installation Date</label>
              <Input type="date" value={machineForm.installationDate || ''} onChange={(e) => setMachineForm(c => ({ ...c, installationDate: e.target.value }))} />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}

      {modal?.entity === 'maintenance' && (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Maintenance' : 'Edit Maintenance'}
          description="Log maintenance tasks."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>Cancel</Button>
              <Button type="submit" form="maintenance-form" disabled={maintenanceMutation.isPending}>
                {modal.mode === 'create' ? 'Create' : 'Save'}
              </Button>
            </>
          }
        >
          <form id="maintenance-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleMaintenanceSubmit}>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Machine</label>
              <Select value={maintenanceForm.machineId} onChange={(e) => setMaintenanceForm(c => ({ ...c, machineId: Number(e.target.value) }))}>
                <option value={0} disabled>Select a machine...</option>
                {machines.map((m) => (
                  <option key={m.id} value={m.id}>{m.name} ({m.machineCode})</option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Type</label>
              <Select value={maintenanceForm.maintenanceType} onChange={(e) => setMaintenanceForm(c => ({ ...c, maintenanceType: e.target.value as MaintenanceType }))}>
                <option value="PREVENTIVE">PREVENTIVE</option>
                <option value="CORRECTIVE">CORRECTIVE</option>
                <option value="PREDICTIVE">PREDICTIVE</option>
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Status</label>
              <Select value={maintenanceForm.status} onChange={(e) => setMaintenanceForm(c => ({ ...c, status: e.target.value as MaintenanceStatus }))}>
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Scheduled Date</label>
              <Input type="date" value={maintenanceForm.scheduledDate || ''} onChange={(e) => setMaintenanceForm(c => ({ ...c, scheduledDate: e.target.value }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Completed Date</label>
              <Input type="date" value={maintenanceForm.completedDate || ''} onChange={(e) => setMaintenanceForm(c => ({ ...c, completedDate: e.target.value }))} />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Remarks</label>
              <Textarea value={maintenanceForm.remarks} onChange={(e) => setMaintenanceForm(c => ({ ...c, remarks: e.target.value }))} placeholder="Notes..." />
            </div>
            {submitError && <div className="text-red-500 text-sm md:col-span-2">{submitError}</div>}
          </form>
        </Modal>
      )}
    </div>
  );
}