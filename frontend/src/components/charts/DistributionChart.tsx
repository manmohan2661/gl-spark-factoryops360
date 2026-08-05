import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

const defaultColors = ['#0f766e', '#0284c7', '#f59e0b', '#ef4444', '#8b5cf6'];

interface DistributionChartProps {
  title: string;
  data?: Array<{ name: string; value: number }>;
}

export function DistributionChart({ title, data }: DistributionChartProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={data ?? []} dataKey="value" nameKey="name" outerRadius={110} innerRadius={65}>
              {(data ?? []).map((entry, index) => (
                <Cell key={entry.name} fill={defaultColors[index % defaultColors.length]} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}