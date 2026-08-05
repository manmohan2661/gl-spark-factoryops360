import { useAuth } from '@/hooks/useAuth';
import { PageHeader } from '@/components/common/PageHeader';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

export function ProfilePage() {
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <PageHeader title="User / Profile Management" description="View the active authenticated session and role context returned by the backend." />

      <Card>
        <CardHeader>
          <CardTitle>Account details</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2">
          <div className="rounded-2xl border bg-muted/30 p-4">
            <p className="text-sm text-muted-foreground">Username</p>
            <p className="mt-2 text-lg font-semibold">{user?.username ?? '—'}</p>
          </div>
          <div className="rounded-2xl border bg-muted/30 p-4">
            <p className="text-sm text-muted-foreground">Role</p>
            <Badge className="mt-2" variant="secondary">
              {user?.role ?? '—'}
            </Badge>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}