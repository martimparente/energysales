import { useMemo, useState } from 'react';
import {
         MRT_EditActionButtons,
         MantineReactTable,
         type MRT_ColumnDef,
         type MRT_Row,
         type MRT_RowData,
         type MRT_TableOptions,
         useMantineReactTable,
} from 'mantine-react-table';
import {
         ActionIcon,
         Button,
         Flex,
         Stack,
         Text,
         Title,
         Tooltip,
} from '@mantine/core';
import { modals } from '@mantine/modals';
import { IconEdit, IconTrash, IconEye } from '@tabler/icons-react';
import { useNavigate } from "react-router-dom";

interface PaginationTableProps<T extends MRT_RowData> {
         columnHeaders: MRT_ColumnDef<T>[];
         data: T[];
         createItem: (item: T) => Promise<Response>;
         updateItem: (item: T) => Promise<Response>;
         deleteItem: (id: string) => Promise<Response>;
         validateItem: (item: T) => Record<string, string | undefined>;
         isLoading: boolean;
         isFetching: boolean;
         isError: boolean;
         resourceName: string;
}

const PaginationTable = <T extends MRT_RowData>({
         columnHeaders,
         data,
         createItem,
         updateItem,
         deleteItem,
         validateItem,
         isLoading,
         isFetching,
         isError,
         resourceName,
}: PaginationTableProps<T>) => {
         const navigate = useNavigate();
         const [validationErrors, setValidationErrors] = useState<Record<string, string | undefined>>({});
         const columns = useMemo<MRT_ColumnDef<T>[]>(() => columnHeaders, [validationErrors]);

         const handleCreateItem: MRT_TableOptions<T>['onCreatingRowSave'] = async ({
                  values,
                  exitCreatingMode,
         }) => {
                  const newValidationErrors = validateItem(values);
                  if (Object.values(newValidationErrors).some((error) => error)) {
                           setValidationErrors(newValidationErrors);
                           return;
                  }
                  setValidationErrors({});
                  await createItem(values);
                  exitCreatingMode();
         };

         const handleSaveItem: MRT_TableOptions<T>['onEditingRowSave'] = async ({
                  values,
                  table,
         }) => {
                  const newValidationErrors = validateItem(values);
                  if (Object.values(newValidationErrors).some((error) => error)) {
                           setValidationErrors(newValidationErrors);
                           return;
                  }
                  setValidationErrors({});
                  await updateItem(values);
                  table.setEditingRow(null); //exit editing mode
         };

         const openDeleteConfirmModal = (row: MRT_Row<T>) =>
                  modals.openConfirmModal({
                           title: `Delete Item?`,
                           children: (
                                    <Text>
                                             Are you sure you want to delete this item? This action cannot be undone.
                                    </Text>
                           ),
                           labels: { confirm: 'Delete', cancel: 'Cancel' },
                           confirmProps: { color: 'red' },
                           onConfirm: () => deleteItem(row.id),
                  });

         const table = useMantineReactTable({
                  columns,
                  data,
                  createDisplayMode: 'modal',
                  editDisplayMode: 'modal',
                  enableEditing: true,
                  getRowId: (row) => row.id,
                  mantineToolbarAlertBannerProps: isError
                           ? {
                                    color: 'red',
                                    children: 'Error loading data',
                           }
                           : undefined,
                  onCreatingRowCancel: () => setValidationErrors({}),
                  onCreatingRowSave: handleCreateItem,
                  onEditingRowCancel: () => setValidationErrors({}),
                  onEditingRowSave: handleSaveItem,
                  renderCreateRowModalContent: ({ table, row, internalEditComponents }) => (
                           <Stack>
                                    <Title order={3}>Create New Item</Title>
                                    {internalEditComponents}
                                    <Flex justify="flex-end" mt="xl">
                                             <MRT_EditActionButtons variant="text" table={table} row={row} />
                                    </Flex>
                           </Stack>
                  ),
                  renderEditRowModalContent: ({ table, row, internalEditComponents }) => (
                           <Stack>
                                    <Title order={3}>Edit Item</Title>
                                    {internalEditComponents}
                                    <Flex justify="flex-end" mt="xl">
                                             <MRT_EditActionButtons variant="text" table={table} row={row} />
                                    </Flex>
                           </Stack>
                  ),
                  renderRowActions: ({ row, table }) => (
                           <Flex gap="md">
                                    <Tooltip label="Show">
                                             <ActionIcon onClick={() => navigate(`/${resourceName}/${row.id}`)}>
                                                      <IconEye />
                                             </ActionIcon>
                                    </Tooltip>
                                    <Tooltip label="Edit">
                                             <ActionIcon onClick={() => table.setEditingRow(row)}>
                                                      <IconEdit />
                                             </ActionIcon>
                                    </Tooltip>
                                    <Tooltip label="Delete">
                                             <ActionIcon color="red" onClick={() => openDeleteConfirmModal(row)}>
                                                      <IconTrash />
                                             </ActionIcon>
                                    </Tooltip>
                           </Flex>
                  ),
                  renderTopToolbarCustomActions: ({ table }) => (
                           <Button onClick={() => table.setCreatingRow(true)}>
                                    Create New Item
                           </Button>
                  ),
                  state: {
                           isLoading,
                           isSaving: false,
                           showAlertBanner: isError,
                           showProgressBars: isFetching,
                  },
         });

         return <MantineReactTable table={table} />;
};

export default PaginationTable;