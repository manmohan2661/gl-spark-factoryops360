export type Role = 'ADMIN' | 'PRODUCTION_MANAGER' | 'INVENTORY_MANAGER' | 'QUALITY_INSPECTOR' | 'SUPPLIER_MANAGER';

const validRoles: Role[] = ['ADMIN', 'PRODUCTION_MANAGER', 'INVENTORY_MANAGER', 'QUALITY_INSPECTOR', 'SUPPLIER_MANAGER'];

export const rolePermissions: Record<Role, string[]> = {
  ADMIN: ['dashboard', 'inventory', 'production', 'quality', 'suppliers', 'alerts', 'profile'],
  PRODUCTION_MANAGER: ['dashboard', 'production', 'alerts', 'profile'],
  INVENTORY_MANAGER: ['dashboard', 'inventory', 'alerts', 'profile'],
  QUALITY_INSPECTOR: ['dashboard', 'quality', 'alerts', 'profile'],
  SUPPLIER_MANAGER: ['dashboard', 'suppliers', 'alerts', 'profile'],
};

export function isRole(value: string | null | undefined): value is Role {
  return Boolean(value && validRoles.includes(value as Role));
}

export function normalizeRole(value: string | null | undefined): Role | null {
  return isRole(value) ? value : null;
}

export function canAccess(role: Role | null, resource: string) {
  if (!role) {
    return false;
  }

  return rolePermissions[role].includes(resource);
}