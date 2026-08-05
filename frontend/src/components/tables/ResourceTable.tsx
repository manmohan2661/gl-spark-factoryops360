import { ReactNode } from 'react';
import { ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select } from '@/components/ui/select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

export interface TableColumn<T> {
  header: string;
  cell: (item: T) => ReactNode;
  className?: string;
}

interface ResourceTableProps<T> {
  title: string;
  data: T[];
  columns: TableColumn<T>[];
  search?: string;
  onSearchChange?: (value: string) => void;
  filterValue?: string;
  onFilterChange?: (value: string) => void;
  filterOptions?: Array<{ label: string; value: string }>;
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  isLoading?: boolean;
  emptyMessage: string;
}

export function ResourceTable<T>({
  title,
  data,
  columns,
  search,
  onSearchChange,
  filterValue,
  onFilterChange,
  filterOptions,
  page,
  totalPages,
  onPageChange,
  isLoading,
  emptyMessage,
}: ResourceTableProps<T>) {
  return (
    <Card>
      <CardHeader className="space-y-4">
        <CardTitle>{title}</CardTitle>
        <div className="flex flex-col gap-3 lg:flex-row lg:items-center">
          {onSearchChange ? (
            <div className="relative max-w-md flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input value={search ?? ''} onChange={(event) => onSearchChange(event.target.value)} placeholder="Search..." className="pl-9" />
            </div>
          ) : null}

          {filterOptions && onFilterChange ? (
            <Select value={filterValue} onChange={(event) => onFilterChange(event.target.value)} className="max-w-xs">
              {filterOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          ) : null}
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="overflow-hidden rounded-xl border">
          <Table>
            <TableHeader>
              <TableRow>
                {columns.map((column) => (
                  <TableHead key={column.header} className={column.className}>
                    {column.header}
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>
            <TableBody>
              {!isLoading && data.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={columns.length} className="py-16 text-center text-muted-foreground">
                    {emptyMessage}
                  </TableCell>
                </TableRow>
              ) : null}

              {data.map((item, index) => (
                <TableRow key={index}>
                  {columns.map((column) => (
                    <TableCell key={column.header} className={column.className}>
                      {column.cell(item)}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>

        <div className="flex items-center justify-between gap-3">
          <p className="text-sm text-muted-foreground">Page {page + 1} of {Math.max(totalPages, 1)}</p>
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
              <ChevronLeft className="h-4 w-4" />
              Prev
            </Button>
            <Button variant="outline" size="sm" disabled={page + 1 >= totalPages} onClick={() => onPageChange(page + 1)}>
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}