import { useState, useMemo } from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Cell
} from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { SupplierResponse, SupplierPerformanceResponse } from '@/types/supplier';

interface SupplierComparisonProps {
  suppliers: SupplierResponse[];
  performances: SupplierPerformanceResponse[];
}

export function SupplierComparison({ suppliers, performances }: SupplierComparisonProps) {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  // Map latest performance to each supplier
  const latestPerformanceBySupplier = useMemo(() => {
    return performances.reduce((map, performance) => {
      const existing = map.get(performance.supplierId);
      if (!existing || new Date(performance.evaluatedAt).getTime() > new Date(existing.evaluatedAt).getTime()) {
        map.set(performance.supplierId, performance);
      }
      return map;
    }, new Map<number, SupplierPerformanceResponse>());
  }, [performances]);

  const activeSuppliers = useMemo(() => {
    return suppliers.filter((s) => s.status === 'ACTIVE');
  }, [suppliers]);

  const toggleSelection = (id: number) => {
    setSelectedIds((prev) => {
      if (prev.includes(id)) {
        return prev.filter((item) => item !== id);
      }
      if (prev.length >= 5) return prev; // Max 5 selection
      return [...prev, id];
    });
  };

  const comparisonData = useMemo(() => {
    return selectedIds.map((id) => {
      const supplier = suppliers.find((s) => s.id === id);
      const perf = latestPerformanceBySupplier.get(id);

      const qualityScore = perf?.qualityScore ?? 0;
      const deliveryRate = perf?.onTimeDeliveryRate ?? 0;
      const defectRate = perf?.defectRate ?? 0;
      const performanceScore = Number(((qualityScore + deliveryRate + Math.max(0, 100 - defectRate)) / 3).toFixed(1));

      return {
        id,
        supplierName: supplier?.name ?? 'Unknown',
        qualityScore,
        deliveryRate,
        defectRate,
        performanceScore,
        inverseDefect: Math.max(0, 100 - defectRate),
      };
    });
  }, [selectedIds, suppliers, latestPerformanceBySupplier]);

  const COLORS = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6'];

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle>Select Suppliers to Compare (2 to 5)</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-48 overflow-y-auto border rounded-md p-4 bg-card">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {activeSuppliers.map((supplier) => (
                <div key={supplier.id} className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    className="w-4 h-4 rounded border-gray-300 text-primary focus:ring-primary"
                    id={`supplier-${supplier.id}`}
                    checked={selectedIds.includes(supplier.id)}
                    onChange={() => toggleSelection(supplier.id)}
                    disabled={!selectedIds.includes(supplier.id) && selectedIds.length >= 5}
                  />
                  <label htmlFor={`supplier-${supplier.id}`} className="text-sm font-medium cursor-pointer">
                    {supplier.name}
                  </label>
                </div>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {selectedIds.length >= 2 ? (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="col-span-1 lg:col-span-2 overflow-x-auto">
            <CardHeader>
              <CardTitle>Metrics Table</CardTitle>
            </CardHeader>
            <CardContent>
              <table className="w-full text-sm text-left border rounded-md">
                <thead className="bg-muted text-muted-foreground border-b">
                  <tr>
                    <th className="px-4 py-3 font-medium">Supplier Name</th>
                    <th className="px-4 py-3 font-medium text-right">Quality Score</th>
                    <th className="px-4 py-3 font-medium text-right">Delivery Rate</th>
                    <th className="px-4 py-3 font-medium text-right">Defect Rate</th>
                    <th className="px-4 py-3 font-medium text-right">Overall Performance</th>
                  </tr>
                </thead>
                <tbody>
                  {comparisonData.map((data, index) => (
                    <tr key={data.id} className="border-b last:border-0 hover:bg-muted/50">
                      <td className="px-4 py-3 font-medium flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index % COLORS.length] }} />
                        {data.supplierName}
                      </td>
                      <td className="px-4 py-3 text-right">{data.qualityScore}%</td>
                      <td className="px-4 py-3 text-right">{data.deliveryRate}%</td>
                      <td className="px-4 py-3 text-right text-destructive">{data.defectRate}%</td>
                      <td className="px-4 py-3 text-right font-bold text-primary">{data.performanceScore}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Overall Performance Comparison</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={comparisonData} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="supplierName" tickLine={false} axisLine={false} />
                  <YAxis tickLine={false} axisLine={false} domain={[0, 100]} />
                  <Tooltip cursor={{ fill: 'rgba(0,0,0,0.05)' }} contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                  <Legend />
                  <Bar dataKey="performanceScore" name="Overall Score" radius={[4, 4, 0, 0]}>
                    {comparisonData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Performance Footprint (Radar)</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <RadarChart outerRadius="70%" data={
                  [
                    { subject: 'Quality', ...comparisonData.reduce((acc, d) => ({ ...acc, [d.supplierName]: d.qualityScore }), {}) },
                    { subject: 'Delivery', ...comparisonData.reduce((acc, d) => ({ ...acc, [d.supplierName]: d.deliveryRate }), {}) },
                    { subject: 'Reliability (Inv. Defect)', ...comparisonData.reduce((acc, d) => ({ ...acc, [d.supplierName]: d.inverseDefect }), {}) }
                  ]
                }>
                  <PolarGrid />
                  <PolarAngleAxis dataKey="subject" />
                  <PolarRadiusAxis angle={30} domain={[0, 100]} />
                  <Tooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                  <Legend />
                  {comparisonData.map((data, index) => (
                    <Radar
                      key={data.id}
                      name={data.supplierName}
                      dataKey={data.supplierName}
                      stroke={COLORS[index % COLORS.length]}
                      fill={COLORS[index % COLORS.length]}
                      fillOpacity={0.3}
                    />
                  ))}
                </RadarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>
      ) : (
        <div className="text-center py-12 text-muted-foreground border border-dashed rounded-lg">
          Please select at least 2 suppliers to view the comparison.
        </div>
      )}
    </div>
  );
}
