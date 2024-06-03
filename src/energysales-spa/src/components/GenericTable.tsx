import React, {useState} from 'react';
import {Alert, Button, Loader, Table} from '@mantine/core';
import {GenericModal} from './GenericModal';

export interface Column<T> {
    header: string;
    accessor: string;
    sortable: boolean;
}

interface GenericTableProps<T, C, U> {
    columns: Column<T>[];
    data: T[] | undefined;
    createResource: (input: C) => Promise<Response>;
    updateResource: (input: U) => Promise<Response>;
    deleteHandler: (item: T) => void;
    onShowClickHandler: (item: T) => void;
    isFetching: boolean;
    error: Error | null;
}

export function GenericTable<T, C, U>(
    {
        columns,
        data,
        createResource,
        updateResource,
        deleteHandler,
        onShowClickHandler,
        isFetching,
        error,
    }: GenericTableProps<T, C, U>) {

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentItem, setCurrentItem] = useState<T | null>(null);

    const initialValues: T = {} as T; // Adjust this based on your data structure
    const fields = columns.map(column => ({
        name: column.accessor,
        label: column.header,
        placeholder: `Enter ${column.header.toLowerCase()}`,
    }));

    const handleCreateResourceForm = async (values: C) => {
        await createResource(values);
        setIsModalOpen(false);
    };

    const handleUpdateResourceForm = async (values: U) => {
        await updateResource(values);
        setIsModalOpen(false);
    };

    const openCreateModal = () => {
        setIsEditMode(false);
        setCurrentItem(null);
        setIsModalOpen(true);
    };

    const openUpdateModal = (item: T) => {
        setIsEditMode(true);
        setCurrentItem(item);
        setIsModalOpen(true);
    };

    if (isFetching) {
        return <Loader/>;
    }

    if (error) {
        return <Alert color="red">{error}</Alert>;
    }

    const renderCellContent = (item: T, accessor: string) => {
        const value = accessor.split('.').reduce((acc, key) => acc && acc[key], item);
        console.log(value)

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
            <Button onClick={openCreateModal} mb="md">Create New</Button>
            <GenericModal<T>
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={isEditMode ? handleUpdateResourceForm : handleCreateResourceForm}
                initialValues={isEditMode && currentItem ? currentItem : initialValues}
                fields={fields}
            />
            <Table>
                <thead>
                <tr>
                    {columns.map((column) => (
                        <th key={column.accessor}>{column.header}</th>
                    ))}
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {data?.map((item) => (
                    <tr key={Math.random()}>
                        {columns.map((column) => (
                            <td key={column.accessor}>
                                {renderCellContent(item, column.accessor)}
                            </td>
                        ))}
                        <td>
                            <Button onClick={() => onShowClickHandler(item)}>Show</Button>
                            <Button onClick={() => openUpdateModal(item)} variant="outline" size="xs"
                                    mr="xs">Edit</Button>
                            <Button onClick={() => deleteHandler(item)} variant="outline" size="xs"
                                    color="red">Delete</Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </div>
    );
}