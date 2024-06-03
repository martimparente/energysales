import React from 'react';
import {Button, Modal, TextInput} from '@mantine/core';
import {useForm} from '@mantine/form';

interface GenericModalProps<T> {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: T) => void;
    initialValues: T;
    fields: Array<{
        name: keyof T;
        label: string;
        placeholder: string;
    }>;
}

export function GenericModal<T>(
    {
        isOpen,
        onClose,
        onSubmit,
        initialValues,
        fields,
    }: GenericModalProps<T>) {
    const form = useForm({
        initialValues,
    });

    const handleSubmit = (values: T) => {
        onSubmit(values);
        onClose();
    };

    return (
        <Modal opened={isOpen} onClose={onClose} title="Create New Resource">
            <form onSubmit={form.onSubmit(handleSubmit)}>
                {fields.map((field) => (
                    <TextInput
                        key={field.name as string}
                        label={field.label}
                        placeholder={field.placeholder}
                        {...form.getInputProps(field.name as string)}
                    />
                ))}
                <Button type="submit" mt="md">Submit</Button>
            </form>
        </Modal>
    );
}