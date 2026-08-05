import { Outlet } from 'react-router-dom';
import { AppSidebar } from '@/components/layouts/AppSidebar';
import { TopBar } from '@/components/layouts/TopBar';

export function AppShell() {
  return (
    <div className="app-grid min-h-screen bg-background text-foreground">
      <div className="mx-auto flex min-h-screen w-full max-w-[1800px]">
        <AppSidebar />
        <div className="flex min-w-0 flex-1 flex-col">
          <TopBar />
          <main className="flex-1 p-4 md:p-6 xl:p-8">
            <div className="animate-fadeIn space-y-6">
              <Outlet />
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}