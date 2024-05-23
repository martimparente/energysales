import React, { useState } from 'react';
import { Table, Button, Loader, Alert } from '@mantine/core';
import { GenericModal } from './GenericModal';

interface Column<T> {
  header: string;
  accessor: keyof T;
}

interface GenericTableProps<T> {
  columns: Column<T>[];
  data: T[];
  createResource: (data: T) => void;
  handleCreateResourceForm: (data: T) => void;
  updateResource: (item: T) => void;
  handleUpdateResourceForm: (item: T) => void;
  deleteResource: (item: T) => void;
  isFetching: boolean;
  error: string | null;
}

export function GenericTable<T extends { id: string | number }>({
  columns,
  data,
  createResource,
  handleCreateResourceForm,
  updateResource,
  handleUpdateResourceForm,
  deleteResource,
  isFetching,
  error,
}: GenericTableProps<T>) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const initialValues: T = {} as T; // Adjust this based on your data structure
  const fields = columns.map(column => ({
    name: column.accessor,
    label: column.header,
    placeholder: `Enter ${column.header.toLowerCase()}`,
  }));

  if (isFetching) {
    return <Loader />;
  }

  if (error) {
    return <Alert color="red">{error}</Alert>;
  }

  const renderCellContent = (item: T, accessor: keyof T) => {
    const value = item[accessor];

    if (typeof value === 'object' && value !== null) {
      return (
        <div>
          {Object.entries(value).map(([key, val]) => (
            <div key={key}>
              <strong>{key}:</strong> {val}
            </div>
          ))}
        </div>
      );
    }

    return value;
  };

  return (
    <div>
      <Button onClick={() => setIsModalOpen(true)} mb="md">Create New</Button>
      <GenericModal<T>
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateResourceForm}
        initialValues={initialValues}
        fields={fields}
      />
      <Table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.accessor as string}>{column.header}</th>
            ))}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              {columns.map((column) => (
                <td key={column.accessor as string}>
                  {renderCellContent(item, column.accessor)}
                </td>
              ))}
              <td>
                <Button onClick={() => alert(JSON.stringify(item, null, 2))} variant="outline" size="xs" mr="xs">Show</Button>
                <Button onClick={() => handleUpdateResourceForm(item)} variant="outline" size="xs" mr="xs">Edit</Button>
                <Button onClick={() => deleteResource(item)} variant="outline" size="xs" color="red">Delete</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}