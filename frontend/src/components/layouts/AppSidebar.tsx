import { NavLink } from 'react-router-dom';
import { Building2, BellRing, Boxes, ClipboardList, LayoutDashboard, Menu, ShieldCheck, Truck, Users, X } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { canAccess } from '@/utils/permissions';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { useState } from 'react';

const navigation = [
  { label: 'Dashboard', to: '/', resource: 'dashboard', icon: LayoutDashboard },
  { label: 'Inventory', to: '/inventory', resource: 'inventory', icon: Boxes },
  { label: 'Production', to: '/production', resource: 'production', icon: ClipboardList },
  { label: 'Quality', to: '/quality', resource: 'quality', icon: ShieldCheck },
  { label: 'Suppliers', to: '/suppliers', resource: 'suppliers', icon: Truck },
  { label: 'Alerts', to: '/alerts', resource: 'alerts', icon: BellRing },
  { label: 'Profile', to: '/profile', resource: 'profile', icon: Users },
];

export function AppSidebar() {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);

  const items = navigation.filter((item) => canAccess(user?.role ?? null, item.resource));
  const roleLabel = user?.role ? user.role.replaceAll('_', ' ') : 'Guest';

  return (
    <>
      <aside className="hidden w-80 border-r bg-card/80 backdrop-blur xl:flex xl:flex-col">
        <div className="flex items-center gap-3 px-6 py-6">
          <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-soft">
            <Building2 className="h-6 w-6" />
          </div>
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-muted-foreground">FactoryOps360</p>
            <p className="text-lg font-semibold">Operations Command Center</p>
          </div>
        </div>

        <div className="px-6">
          <div className="rounded-2xl border bg-muted/30 p-4">
            <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">Signed in as</p>
            <div className="mt-2 flex items-center justify-between gap-3">
              <div>
                <p className="font-semibold">{user?.username}</p>
                <Badge variant="secondary" className="mt-2">
                  {roleLabel}
                </Badge>
              </div>
            </div>
          </div>
        </div>

        <nav className="mt-6 flex-1 space-y-1 px-4">
          {items.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-colors ${
                    isActive ? 'bg-primary text-primary-foreground shadow-soft' : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                  }`
                }
              >
                <Icon className="h-4 w-4" />
                {item.label}
              </NavLink>
            );
          })}
        </nav>

        <div className="space-y-4 p-6">
          <Separator />
          <Button variant="outline" className="w-full justify-start" onClick={logout}>
            Logout
          </Button>
          <p className="text-xs text-muted-foreground">Role-aware navigation is enforced from the backend claims.</p>
        </div>
      </aside>

      <div className="xl:hidden">
        <div className="sticky top-0 z-40 border-b bg-card/90 backdrop-blur">
          <div className="flex items-center justify-between px-4 py-3">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary text-primary-foreground">
                <Building2 className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm font-semibold">FactoryOps360</p>
                <p className="text-xs text-muted-foreground">Command Center</p>
              </div>
            </div>
            <Button variant="outline" size="icon" onClick={() => setOpen(true)}>
              <Menu className="h-4 w-4" />
            </Button>
          </div>
        </div>

        {open ? (
          <div className="fixed inset-0 z-50 bg-black/40">
            <div className="absolute inset-y-0 right-0 w-[88%] max-w-sm border-l bg-background p-4 shadow-soft">
              <div className="flex items-center justify-between">
                <p className="font-semibold">Navigation</p>
                <Button variant="ghost" size="icon" onClick={() => setOpen(false)}>
                  <X className="h-4 w-4" />
                </Button>
              </div>
              <Separator className="my-4" />
              <div className="space-y-2">
                {items.map((item) => {
                  const Icon = item.icon;

                  return (
                    <NavLink
                      key={item.to}
                      to={item.to}
                      end={item.to === '/'}
                      onClick={() => setOpen(false)}
                      className={({ isActive }) =>
                        `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-colors ${
                          isActive ? 'bg-primary text-primary-foreground' : 'bg-muted/40 text-foreground'
                        }`
                      }
                    >
                      <Icon className="h-4 w-4" />
                      {item.label}
                    </NavLink>
                  );
                })}
              </div>
              <div className="mt-6 space-y-3">
                <Button variant="outline" className="w-full justify-start" onClick={logout}>
                  Logout
                </Button>
                <p className="text-xs text-muted-foreground">Role: {roleLabel}</p>
              </div>
            </div>
          </div>
        ) : null}
      </div>
    </>
  );
}