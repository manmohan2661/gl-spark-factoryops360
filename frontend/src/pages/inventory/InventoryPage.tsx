import { useMemo, useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Boxes, PencilLine, Plus, Trash2, Warehouse } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { inventoryApi } from '@/api/inventoryApi';
import { ErrorState } from '@/components/common/ErrorState';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { LoadingState } from '@/components/common/LoadingState';
import { Modal } from '@/components/common/Modal';
import { PageHeader } from '@/components/common/PageHeader';
import { ResourceTable } from '@/components/tables/ResourceTable';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DistributionChart } from '@/components/charts/DistributionChart';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Select } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { formatDateTime } from '@/utils/date';
import type {
  InventoryRequest,
  InventoryResponse,
  MaterialRequest,
  MaterialResponse,
  UnitOfMeasure,
  WarehouseRequest,
  WarehouseResponse,
} from '@/types/inventory';

interface InventoryItemView {
  id: number;
  materialId: number;
  warehouseId: number;
  materialCode: string;
  materialName: string;
  warehouse: string;
  stock: number;
  reserved: number;
  minStock: number;
  status: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  updatedAt: string;
}

interface MaterialView {
  id: number;
  code: string;
  name: string;
  description: string;
  category: string;
  unitOfMeasure: UnitOfMeasure;
  reorderLevel: number;
  active: boolean;
  updatedAt: string;
}

interface WarehouseView {
  id: number;
  code: string;
  name: string;
  location: string;
  capacity: number;
  active: boolean;
  stockCount: number;
  updatedAt: string;
}

type ActiveEntity = 'material' | 'warehouse' | 'inventory';
type ActiveFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';
type InventoryFilter = 'ALL' | 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
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

function deriveInventoryStatus(stock: number, minStock: number) {
  if (stock <= 0) {
    return 'OUT_OF_STOCK' as const;
  }

  if (stock <= minStock) {
    return 'LOW_STOCK' as const;
  }

  return 'IN_STOCK' as const;
}

function emptyMaterialForm(): MaterialRequest {
  return {
    code: '',
    name: '',
    description: '',
    unitOfMeasure: 'PIECE',
    category: '',
    reorderLevel: 0,
    active: true,
  };
}

function emptyWarehouseForm(): WarehouseRequest {
  return {
    code: '',
    name: '',
    location: '',
    capacity: 0,
    active: true,
  };
}

function emptyInventoryForm(): InventoryRequest {
  return {
    quantityAvailable: 0,
    quantityReserved: 0,
    materialId: 0,
    warehouseId: 0,
  };
}

function normalizeSearch(value: string) {
  return value.trim().toLowerCase();
}

export function InventoryPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [modal, setModal] = useState<ModalState | null>(null);
  const [deleteState, setDeleteState] = useState<DeleteState | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const [materialSearch, setMaterialSearch] = useState('');
  const [materialFilter, setMaterialFilter] = useState<ActiveFilter>('ALL');
  const [materialPage, setMaterialPage] = useState(0);
  const [materialForm, setMaterialForm] = useState<MaterialRequest>(emptyMaterialForm());

  const [warehouseSearch, setWarehouseSearch] = useState('');
  const [warehouseFilter, setWarehouseFilter] = useState<ActiveFilter>('ALL');
  const [warehousePage, setWarehousePage] = useState(0);
  const [warehouseForm, setWarehouseForm] = useState<WarehouseRequest>(emptyWarehouseForm());

  const [inventorySearch, setInventorySearch] = useState('');
  const [inventoryFilter, setInventoryFilter] = useState<InventoryFilter>('ALL');
  const [inventoryPage, setInventoryPage] = useState(0);
  const [inventoryForm, setInventoryForm] = useState<InventoryRequest>(emptyInventoryForm());

  const inventoriesQuery = useQuery({ queryKey: ['inventory-inventories'], queryFn: inventoryApi.getInventories });
  const materialsQuery = useQuery({ queryKey: ['inventory-materials'], queryFn: inventoryApi.getMaterials });
  const warehousesQuery = useQuery({ queryKey: ['inventory-warehouses'], queryFn: inventoryApi.getWarehouses });

  const inventories = inventoriesQuery.data ?? [];
  const materials = materialsQuery.data ?? [];
  const warehouses = warehousesQuery.data ?? [];

  const canManageInventory = user?.role === 'ADMIN' || user?.role === 'INVENTORY_MANAGER';

  const materialById = useMemo(() => new Map<number, MaterialResponse>(materials.map((material) => [material.id, material])), [materials]);
  const warehouseById = useMemo(() => new Map<number, WarehouseResponse>(warehouses.map((warehouse) => [warehouse.id, warehouse])), [warehouses]);

  const materialRows: MaterialView[] = useMemo(
    () =>
      materials.map((material) => ({
        id: material.id,
        code: material.code,
        name: material.name,
        description: material.description,
        category: material.category,
        unitOfMeasure: material.unitOfMeasure,
        reorderLevel: material.reorderLevel,
        active: material.active,
        updatedAt: material.updatedAt,
      })),
    [materials],
  );

  const inventoryRows: InventoryItemView[] = useMemo(
    () =>
      inventories.map((inventory: InventoryResponse) => {
        const material = materialById.get(inventory.materialId);
        const warehouse = warehouseById.get(inventory.warehouseId);
        const minStock = material?.reorderLevel ?? 0;
        const stock = inventory.quantityAvailable;

        return {
          id: inventory.id,
          materialId: inventory.materialId,
          warehouseId: inventory.warehouseId,
          materialCode: material?.code ?? `M-${inventory.materialId}`,
          materialName: material?.name ?? `Material ${inventory.materialId}`,
          warehouse: warehouse?.name ?? `Warehouse ${inventory.warehouseId}`,
          stock,
          reserved: inventory.quantityReserved,
          minStock,
          status: deriveInventoryStatus(stock, minStock),
          updatedAt: inventory.lastUpdated,
        };
      }),
    [inventories, materialById, warehouseById],
  );

  const warehouseRows: WarehouseView[] = useMemo(
    () =>
      warehouses.map((warehouse) => ({
        id: warehouse.id,
        code: warehouse.code,
        name: warehouse.name,
        location: warehouse.location,
        capacity: warehouse.capacity,
        active: warehouse.active,
        stockCount: inventoryRows.filter((item) => item.warehouseId === warehouse.id).reduce((total, item) => total + item.stock, 0),
        updatedAt: warehouse.updatedAt,
      })),
    [inventoryRows, warehouses],
  );

  const materialMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: MaterialRequest }) => {
      if (variables.id) {
        return inventoryApi.updateMaterial(variables.id, variables.payload);
      }

      return inventoryApi.createMaterial(variables.payload);
    },
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['inventory-materials'] }),
        queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] }),
      ]);
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save material. Check the payload and try again.'),
  });

  const warehouseMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: WarehouseRequest }) => {
      if (variables.id) {
        return inventoryApi.updateWarehouse(variables.id, variables.payload);
      }

      return inventoryApi.createWarehouse(variables.payload);
    },
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['inventory-warehouses'] }),
        queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] }),
      ]);
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save warehouse. Check the payload and try again.'),
  });

  const inventoryMutation = useMutation({
    mutationFn: async (variables: { id?: number; payload: InventoryRequest }) => {
      if (variables.id) {
        return inventoryApi.updateInventory(variables.id, variables.payload);
      }

      return inventoryApi.createInventory(variables.payload);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] });
      setModal(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to save inventory. Check the payload and try again.'),
  });

  const deleteMaterialMutation = useMutation({
    mutationFn: inventoryApi.deleteMaterial,
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['inventory-materials'] }),
        queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] }),
      ]);
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the material record.'),
  });

  const deleteWarehouseMutation = useMutation({
    mutationFn: inventoryApi.deleteWarehouse,
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['inventory-warehouses'] }),
        queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] }),
      ]);
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the warehouse record.'),
  });

  const deleteInventoryMutation = useMutation({
    mutationFn: inventoryApi.deleteInventory,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['inventory-inventories'] });
      setDeleteState(null);
      setSubmitError(null);
    },
    onError: () => setSubmitError('Unable to delete the inventory record.'),
  });

  if (inventoriesQuery.isLoading || materialsQuery.isLoading || warehousesQuery.isLoading) {
    return <LoadingState />;
  }

  if (inventoriesQuery.isError || materialsQuery.isError || warehousesQuery.isError) {
    return (
      <ErrorState
        description="Inventory data could not be loaded from the API gateway."
        onRetry={() => {
          inventoriesQuery.refetch();
          materialsQuery.refetch();
          warehousesQuery.refetch();
        }}
      />
    );
  }

  const filteredMaterials = materialRows.filter((item) => {
    const matchesSearch = `${item.code} ${item.name} ${item.description} ${item.category} ${item.unitOfMeasure}`.toLowerCase().includes(normalizeSearch(materialSearch));
    const matchesFilter = materialFilter === 'ALL' || (materialFilter === 'ACTIVE' ? item.active : !item.active);
    return matchesSearch && matchesFilter;
  });

  const filteredWarehouses = warehouseRows.filter((item) => {
    const matchesSearch = `${item.code} ${item.name} ${item.location}`.toLowerCase().includes(normalizeSearch(warehouseSearch));
    const matchesFilter = warehouseFilter === 'ALL' || (warehouseFilter === 'ACTIVE' ? item.active : !item.active);
    return matchesSearch && matchesFilter;
  });

  const filteredInventories = inventoryRows.filter((item) => {
    const matchesSearch = `${item.materialCode} ${item.materialName} ${item.warehouse} ${item.status} ${item.materialId} ${item.warehouseId}`.toLowerCase().includes(normalizeSearch(inventorySearch));
    const matchesFilter = inventoryFilter === 'ALL' || item.status === inventoryFilter;
    return matchesSearch && matchesFilter;
  });

  const pageSize = 10;
  const materialTotalPages = Math.max(1, Math.ceil(filteredMaterials.length / pageSize));
  const warehouseTotalPages = Math.max(1, Math.ceil(filteredWarehouses.length / pageSize));
  const inventoryTotalPages = Math.max(1, Math.ceil(filteredInventories.length / pageSize));

  const pagedMaterials = filteredMaterials.slice(materialPage * pageSize, (materialPage + 1) * pageSize);
  const pagedWarehouses = filteredWarehouses.slice(warehousePage * pageSize, (warehousePage + 1) * pageSize);
  const pagedInventories = filteredInventories.slice(inventoryPage * pageSize, (inventoryPage + 1) * pageSize);

  const summary = {
    totalMaterials: materials.length,
    activeWarehouses: warehouses.filter((warehouse) => warehouse.active).length,
    totalStock: inventoryRows.reduce((total, item) => total + item.stock, 0),
    lowStockMaterials: inventoryRows.filter((item) => item.status === 'LOW_STOCK').length,
    outOfStockMaterials: inventoryRows.filter((item) => item.status === 'OUT_OF_STOCK').length,
  };

  const statusDistribution = [
    { name: 'In stock', value: inventoryRows.filter((item) => item.status === 'IN_STOCK').length },
    { name: 'Low stock', value: inventoryRows.filter((item) => item.status === 'LOW_STOCK').length },
    { name: 'Out of stock', value: inventoryRows.filter((item) => item.status === 'OUT_OF_STOCK').length },
  ];

  const warehouseDistribution = warehouseRows
    .map((warehouse) => ({ name: warehouse.name, value: warehouse.stockCount }))
    .filter((entry) => entry.value > 0)
    .slice(0, 6);

  function closeModal() {
    setModal(null);
    setSubmitError(null);
  }

  function openMaterialCreate() {
    setMaterialForm(emptyMaterialForm());
    setSubmitError(null);
    setModal({ entity: 'material', mode: 'create' });
  }

  function openMaterialEdit(item: MaterialView) {
    setMaterialForm({
      code: item.code,
      name: item.name,
      description: item.description,
      unitOfMeasure: item.unitOfMeasure,
      category: item.category,
      reorderLevel: item.reorderLevel,
      active: item.active,
    });
    setSubmitError(null);
    setModal({ entity: 'material', mode: 'edit', id: item.id });
  }

  function openWarehouseCreate() {
    setWarehouseForm(emptyWarehouseForm());
    setSubmitError(null);
    setModal({ entity: 'warehouse', mode: 'create' });
  }

  function openWarehouseEdit(item: WarehouseView) {
    setWarehouseForm({
      code: item.code,
      name: item.name,
      location: item.location,
      capacity: item.capacity,
      active: item.active,
    });
    setSubmitError(null);
    setModal({ entity: 'warehouse', mode: 'edit', id: item.id });
  }

  function openInventoryCreate() {
    setInventoryForm({
      quantityAvailable: 0,
      quantityReserved: 0,
      materialId: materials[0]?.id ?? 0,
      warehouseId: warehouses[0]?.id ?? 0,
    });
    setSubmitError(null);
    setModal({ entity: 'inventory', mode: 'create' });
  }

  function openInventoryEdit(item: InventoryItemView) {
    const currentInventory = inventories.find((inventory) => inventory.id === item.id);
    setInventoryForm({
      quantityAvailable: currentInventory?.quantityAvailable ?? item.stock,
      quantityReserved: currentInventory?.quantityReserved ?? item.reserved,
      materialId: item.materialId,
      warehouseId: item.warehouseId,
    });
    setSubmitError(null);
    setModal({ entity: 'inventory', mode: 'edit', id: item.id });
  }

  function openDeleteDialog(entity: ActiveEntity, id: number, label: string) {
    setDeleteState({ entity, id, label });
    setSubmitError(null);
  }

  async function handleMaterialSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!materialForm.code.trim() || !materialForm.name.trim() || !materialForm.category.trim()) {
      setSubmitError('Material code, name, and category are required.');
      return;
    }

    try {
      await materialMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload: materialForm });
    } catch {
      // handled by mutation state
    }
  }

  async function handleWarehouseSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!warehouseForm.code.trim() || !warehouseForm.name.trim() || !warehouseForm.location.trim()) {
      setSubmitError('Warehouse code, name, and location are required.');
      return;
    }

    try {
      await warehouseMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload: warehouseForm });
    } catch {
      // handled by mutation state
    }
  }

  async function handleInventorySubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (inventoryForm.materialId <= 0 || inventoryForm.warehouseId <= 0) {
      setSubmitError('Select both a material and a warehouse.');
      return;
    }

    try {
      await inventoryMutation.mutateAsync({ id: modal?.mode === 'edit' ? modal.id : undefined, payload: inventoryForm });
    } catch {
      // handled by mutation state
    }
  }

  async function handleDeleteConfirm() {
    if (!deleteState) {
      return;
    }

    try {
      if (deleteState.entity === 'material') {
        await deleteMaterialMutation.mutateAsync(deleteState.id);
      } else if (deleteState.entity === 'warehouse') {
        await deleteWarehouseMutation.mutateAsync(deleteState.id);
      } else {
        await deleteInventoryMutation.mutateAsync(deleteState.id);
      }
    } catch {
      // handled by mutation state
    }
  }

  const materialFilterOptions = [
    { label: 'All materials', value: 'ALL' },
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' },
  ];

  const warehouseFilterOptions = [
    { label: 'All warehouses', value: 'ALL' },
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' },
  ];

  const inventoryFilterOptions = [
    { label: 'All inventory', value: 'ALL' },
    { label: 'In stock', value: 'IN_STOCK' },
    { label: 'Low stock', value: 'LOW_STOCK' },
    { label: 'Out of stock', value: 'OUT_OF_STOCK' },
  ];

  const pageActions = canManageInventory ? (
    <div className="flex flex-wrap justify-end gap-2">
      <Button variant="outline" onClick={openMaterialCreate}>
        <Plus className="h-4 w-4" />
        Add Material
      </Button>
      <Button variant="outline" onClick={openWarehouseCreate}>
        <Warehouse className="h-4 w-4" />
        Add Warehouse
      </Button>
      <Button onClick={openInventoryCreate} disabled={materials.length === 0 || warehouses.length === 0}>
        <Boxes className="h-4 w-4" />
        Add Inventory
      </Button>
    </div>
  ) : null;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Inventory Management"
        description="Manage materials, warehouses, and on-hand inventory from a single manufacturing operations console."
        actions={pageActions}
      />

      {submitError ? (
        <Card className="border-red-200 bg-red-50/70">
          <CardContent className="p-4 text-sm text-red-700">{submitError}</CardContent>
        </Card>
      ) : null}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Materials</p><p className="mt-2 text-3xl font-semibold">{summary.totalMaterials}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Active warehouses</p><p className="mt-2 text-3xl font-semibold">{summary.activeWarehouses}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Total stock</p><p className="mt-2 text-3xl font-semibold">{summary.totalStock}</p></CardContent></Card>
        <Card><CardContent className="p-6"><p className="text-sm text-muted-foreground">Low / out stock</p><p className="mt-2 text-3xl font-semibold">{summary.lowStockMaterials + summary.outOfStockMaterials}</p></CardContent></Card>
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <DistributionChart title="Inventory status mix" data={statusDistribution} />
        <DistributionChart title="Stock by warehouse" data={warehouseDistribution} />
      </div>

      <ResourceTable
        title="Material master"
        data={pagedMaterials}
        columns={[
          { header: 'Material', cell: (item) => <div><div className="font-medium">{item.name}</div><div className="text-xs text-muted-foreground">{item.code}</div></div> },
          { header: 'Category', cell: (item) => item.category },
          { header: 'UOM', cell: (item) => item.unitOfMeasure },
          { header: 'Reorder', cell: (item) => item.reorderLevel },
          { header: 'Status', cell: (item) => <StatusBadge value={item.active ? 'ACTIVE' : 'INACTIVE'} /> },
          { header: 'Updated', cell: (item) => formatDateTime(item.updatedAt) },
          ...(canManageInventory
            ? [
                {
                  header: 'Actions',
                  cell: (item: MaterialView) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openMaterialEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('material', item.id, `${item.name} (${item.code})`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={materialSearch}
        onSearchChange={(value) => {
          setMaterialPage(0);
          setMaterialSearch(value);
        }}
        filterValue={materialFilter}
        onFilterChange={(value) => {
          setMaterialPage(0);
          setMaterialFilter(value as ActiveFilter);
        }}
        filterOptions={materialFilterOptions}
        page={materialPage}
        totalPages={materialTotalPages}
        onPageChange={setMaterialPage}
        emptyMessage="No materials were returned by the backend."
      />

      <ResourceTable
        title="Warehouse directory"
        data={pagedWarehouses}
        columns={[
          { header: 'Warehouse', cell: (item) => <div><div className="font-medium">{item.name}</div><div className="text-xs text-muted-foreground">{item.code}</div></div> },
          { header: 'Location', cell: (item) => item.location },
          { header: 'Capacity', cell: (item) => item.capacity },
          { header: 'Stock', cell: (item) => item.stockCount },
          { header: 'Status', cell: (item) => <StatusBadge value={item.active ? 'ACTIVE' : 'INACTIVE'} /> },
          { header: 'Updated', cell: (item) => formatDateTime(item.updatedAt) },
          ...(canManageInventory
            ? [
                {
                  header: 'Actions',
                  cell: (item: WarehouseView) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openWarehouseEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('warehouse', item.id, `${item.name} (${item.code})`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={warehouseSearch}
        onSearchChange={(value) => {
          setWarehousePage(0);
          setWarehouseSearch(value);
        }}
        filterValue={warehouseFilter}
        onFilterChange={(value) => {
          setWarehousePage(0);
          setWarehouseFilter(value as ActiveFilter);
        }}
        filterOptions={warehouseFilterOptions}
        page={warehousePage}
        totalPages={warehouseTotalPages}
        onPageChange={setWarehousePage}
        emptyMessage="No warehouses were returned by the backend."
      />

      <ResourceTable
        title="Inventory balances"
        data={pagedInventories}
        columns={[
          { header: 'Material', cell: (item) => <div><div className="font-medium">{item.materialName}</div><div className="text-xs text-muted-foreground">{item.materialCode}</div></div> },
          { header: 'Warehouse', cell: (item) => item.warehouse },
          { header: 'Available', cell: (item) => item.stock },
          { header: 'Reserved', cell: (item) => item.reserved },
          { header: 'Minimum', cell: (item) => item.minStock },
          { header: 'Status', cell: (item) => <StatusBadge value={item.status} /> },
          { header: 'Updated', cell: (item) => formatDateTime(item.updatedAt) },
          ...(canManageInventory
            ? [
                {
                  header: 'Actions',
                  cell: (item: InventoryItemView) => (
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => openInventoryEdit(item)}>
                        <PencilLine className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => openDeleteDialog('inventory', item.id, `${item.materialName} @ ${item.warehouse}`)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ),
                },
              ]
            : []),
        ]}
        search={inventorySearch}
        onSearchChange={(value) => {
          setInventoryPage(0);
          setInventorySearch(value);
        }}
        filterValue={inventoryFilter}
        onFilterChange={(value) => {
          setInventoryPage(0);
          setInventoryFilter(value as InventoryFilter);
        }}
        filterOptions={inventoryFilterOptions}
        page={inventoryPage}
        totalPages={inventoryTotalPages}
        onPageChange={setInventoryPage}
        emptyMessage="No inventory items were returned by the backend."
      />

      {modal?.entity === 'material' ? (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Material' : 'Edit Material'}
          description="Maintain material master data used across the inventory service."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>
                Cancel
              </Button>
              <Button type="submit" form="material-form" disabled={materialMutation.isPending}>
                {modal.mode === 'create' ? 'Create Material' : 'Save Material'}
              </Button>
            </>
          }
        >
          <form id="material-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleMaterialSubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Code</label>
              <Input value={materialForm.code} onChange={(event) => setMaterialForm((current) => ({ ...current, code: event.target.value }))} placeholder="MAT-1001" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Name</label>
              <Input value={materialForm.name} onChange={(event) => setMaterialForm((current) => ({ ...current, name: event.target.value }))} placeholder="Steel Coil" />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Description</label>
              <Textarea value={materialForm.description} onChange={(event) => setMaterialForm((current) => ({ ...current, description: event.target.value }))} placeholder="Material description" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Category</label>
              <Input value={materialForm.category} onChange={(event) => setMaterialForm((current) => ({ ...current, category: event.target.value }))} placeholder="Raw Material" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Unit of Measure</label>
              <Select value={materialForm.unitOfMeasure} onChange={(event) => setMaterialForm((current) => ({ ...current, unitOfMeasure: event.target.value as UnitOfMeasure }))}>
                <option value="KG">KG</option>
                <option value="GRAM">GRAM</option>
                <option value="LITER">LITER</option>
                <option value="MILLILITER">MILLILITER</option>
                <option value="PIECE">PIECE</option>
                <option value="BOX">BOX</option>
                <option value="METER">METER</option>
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Reorder level</label>
              <Input type="number" min={0} value={materialForm.reorderLevel} onChange={(event) => setMaterialForm((current) => ({ ...current, reorderLevel: Number(event.target.value) }))} />
            </div>
            <div className="flex items-center gap-2 md:col-span-2">
              <Input id="material-active" type="checkbox" checked={materialForm.active} onChange={(event) => setMaterialForm((current) => ({ ...current, active: event.target.checked }))} className="h-4 w-4" />
              <label htmlFor="material-active" className="text-sm font-medium">Active</label>
            </div>
          </form>
        </Modal>
      ) : null}

      {modal?.entity === 'warehouse' ? (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Warehouse' : 'Edit Warehouse'}
          description="Keep warehouse master data aligned with the physical site structure."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>
                Cancel
              </Button>
              <Button type="submit" form="warehouse-form" disabled={warehouseMutation.isPending}>
                {modal.mode === 'create' ? 'Create Warehouse' : 'Save Warehouse'}
              </Button>
            </>
          }
        >
          <form id="warehouse-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleWarehouseSubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Code</label>
              <Input value={warehouseForm.code} onChange={(event) => setWarehouseForm((current) => ({ ...current, code: event.target.value }))} placeholder="WH-01" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Name</label>
              <Input value={warehouseForm.name} onChange={(event) => setWarehouseForm((current) => ({ ...current, name: event.target.value }))} placeholder="Main Warehouse" />
            </div>
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm font-medium">Location</label>
              <Input value={warehouseForm.location} onChange={(event) => setWarehouseForm((current) => ({ ...current, location: event.target.value }))} placeholder="Plant 1, Bay A" />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Capacity</label>
              <Input type="number" min={0} value={warehouseForm.capacity} onChange={(event) => setWarehouseForm((current) => ({ ...current, capacity: Number(event.target.value) }))} />
            </div>
            <div className="flex items-center gap-2 md:col-span-2">
              <Input id="warehouse-active" type="checkbox" checked={warehouseForm.active} onChange={(event) => setWarehouseForm((current) => ({ ...current, active: event.target.checked }))} className="h-4 w-4" />
              <label htmlFor="warehouse-active" className="text-sm font-medium">Active</label>
            </div>
          </form>
        </Modal>
      ) : null}

      {modal?.entity === 'inventory' ? (
        <Modal
          open
          title={modal.mode === 'create' ? 'Add Inventory' : 'Edit Inventory'}
          description="Bind a material to a warehouse and maintain available and reserved quantities."
          onClose={closeModal}
          footer={
            <>
              <Button variant="outline" onClick={closeModal}>
                Cancel
              </Button>
              <Button type="submit" form="inventory-form" disabled={inventoryMutation.isPending || materials.length === 0 || warehouses.length === 0}>
                {modal.mode === 'create' ? 'Create Inventory' : 'Save Inventory'}
              </Button>
            </>
          }
        >
          <form id="inventory-form" className="grid gap-4 md:grid-cols-2" onSubmit={handleInventorySubmit}>
            <div className="space-y-2">
              <label className="text-sm font-medium">Material</label>
              <Select value={String(inventoryForm.materialId)} onChange={(event) => setInventoryForm((current) => ({ ...current, materialId: Number(event.target.value) }))}>
                <option value="0">Select material</option>
                {materials.map((material) => (
                  <option key={material.id} value={material.id}>
                    {material.code} - {material.name}
                  </option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Warehouse</label>
              <Select value={String(inventoryForm.warehouseId)} onChange={(event) => setInventoryForm((current) => ({ ...current, warehouseId: Number(event.target.value) }))}>
                <option value="0">Select warehouse</option>
                {warehouses.map((warehouse) => (
                  <option key={warehouse.id} value={warehouse.id}>
                    {warehouse.code} - {warehouse.name}
                  </option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Available quantity</label>
              <Input type="number" min={0} value={inventoryForm.quantityAvailable} onChange={(event) => setInventoryForm((current) => ({ ...current, quantityAvailable: Number(event.target.value) }))} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Reserved quantity</label>
              <Input type="number" min={0} value={inventoryForm.quantityReserved} onChange={(event) => setInventoryForm((current) => ({ ...current, quantityReserved: Number(event.target.value) }))} />
            </div>
            {materials.length === 0 || warehouses.length === 0 ? (
              <div className="md:col-span-2 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
                Inventory records require at least one material and one warehouse.
              </div>
            ) : null}
          </form>
        </Modal>
      ) : null}

      <ConfirmDialog
        open={Boolean(deleteState)}
        title={deleteState ? `Delete ${deleteState.entity}` : 'Delete record'}
        description={deleteState ? `Delete ${deleteState.label}?` : 'Delete this record?'}
        confirmLabel={deleteState?.entity === 'inventory' ? 'Delete Inventory' : deleteState?.entity === 'warehouse' ? 'Delete Warehouse' : 'Delete Material'}
        onConfirm={handleDeleteConfirm}
        onClose={() => setDeleteState(null)}
      />
    </div>
  );
}
