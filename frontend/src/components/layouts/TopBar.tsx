import { Bell, MoonStar, Search, SunMedium } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { useTheme } from '@/hooks/useTheme';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

export function TopBar() {
  const { user } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="sticky top-0 z-30 border-b bg-background/85 backdrop-blur">
      <div className="flex items-center gap-4 px-4 py-4 md:px-6 xl:px-8">
        <div className="hidden max-w-xl flex-1 items-center gap-3 rounded-2xl border bg-card px-4 py-2 md:flex">
          <Search className="h-4 w-4 text-muted-foreground" />
          <Input className="h-8 border-0 bg-transparent px-0 shadow-none focus-visible:ring-0" placeholder="Search operations, assets, orders, suppliers..." />
        </div>

        <div className="ml-auto flex items-center gap-2">
          <Button variant="outline" size="icon" onClick={toggleTheme} aria-label="Toggle theme">
            {theme === 'dark' ? <SunMedium className="h-4 w-4" /> : <MoonStar className="h-4 w-4" />}
          </Button>
          <Button variant="outline" size="icon" aria-label="Notifications">
            <Bell className="h-4 w-4" />
          </Button>
          <div className="hidden text-right md:block">
            <p className="text-sm font-semibold leading-none">{user?.username}</p>
            <p className="text-xs text-muted-foreground">{user?.role}</p>
          </div>
        </div>
      </div>
    </header>
  );
}