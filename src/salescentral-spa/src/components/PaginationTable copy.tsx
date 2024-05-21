import { useMemo, useState } from 'react';
import {
         MRT_EditActionButtons,
         MantineReactTable,
         type MRT_ColumnDef,
         type MRT_Row,
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
import { useGetTeams, useCreateTeam, useUpdateTeam, useDeleteTeam } from '../services/TeamsService';
import { Team } from "../interfaces/Teams";

const PaginationTable = ({ columnHeaders }: { columnHeaders: MRT_ColumnDef<Team>[] }) => {
         const navigate = useNavigate();
         const [validationErrors, setValidationErrors] = useState<Record<string, string | undefined>>({});
         const columns = useMemo<MRT_ColumnDef<Team>[]>(() => columnHeaders, [validationErrors]);
         const { mutateAsync: createTeam, isPending: isCreatingteam } = useCreateTeam();
         const {
                  data: fetchedteams = [],
                  isError: isLoadingteamsError,
                  isFetching: isFetchingteams,
                  isLoading: isLoadingteams,
         } = useGetTeams();
         const { mutateAsync: updateTeam, isPending: isUpdatingteam } = useUpdateTeam();
         const { mutateAsync: deleteTeam, isPending: isDeletingteam } = useDeleteTeam();

         //CREATE action
         const handleCreateTeam: MRT_TableOptions<Team>['onCreatingRowSave'] = async ({
                  values,
                  exitCreatingMode,
         }) => {
                  const newValidationErrors = validateTeam(values);
                  if (Object.values(newValidationErrors).some((error) => error)) {
                           setValidationErrors(newValidationErrors);
                           return;
                  }
                  setValidationErrors({});
                  await createTeam(values);
                  exitCreatingMode();
         };

         //UPDATE action
         const handleSaveTeam: MRT_TableOptions<Team>['onEditingRowSave'] = async ({
                  values,
                  table,
         }) => {
                  const newValidationErrors = validateTeam(values);
                  if (Object.values(newValidationErrors).some((error) => error)) {
                           setValidationErrors(newValidationErrors);
                           return;
                  }
                  setValidationErrors({});
                  await updateTeam(values);
                  table.setEditingRow(null); //exit editing mode
         };

         //DELETE action
         const openDeleteConfirmModal = (row: MRT_Row<Team>) =>
                  modals.openConfirmModal({
                           title: `Delete Team ${row.original.name}?`,
                           children: (
                                    <Text>
                                             Are you sure you want to delete {row.original.name}?
                                             This action cannot be undone.
                                    </Text>
                           ),
                           labels: { confirm: 'Delete', cancel: 'Cancel' },
                           confirmProps: { color: 'red' },
                           onConfirm: () => deleteTeam(row.original.id.toString()),
                  });

         const table = useMantineReactTable({
                  columns,
                  data: fetchedteams,
                  createDisplayMode: 'modal', //default ('row', and 'custom' are also available)
                  editDisplayMode: 'modal', //default ('row', 'cell', 'table', and 'custom' are also available)
                  enableEditing: true,
                  getRowId: (row) => row.id,
                  mantineToolbarAlertBannerProps: isLoadingteamsError
                           ? {
                                    color: 'red',
                                    children: 'Error loading data',
                           }
                           : undefined,

                  onCreatingRowCancel: () => setValidationErrors({}),
                  onCreatingRowSave: handleCreateTeam,
                  onEditingRowCancel: () => setValidationErrors({}),
                  onEditingRowSave: handleSaveTeam,
                  renderCreateRowModalContent: ({ table, row, internalEditComponents }) => (
                           <Stack>
                                    <Title order={3}>Create New Team</Title>
                                    {internalEditComponents}
                                    <Flex justify="flex-end" mt="xl">
                                             <MRT_EditActionButtons variant="text" table={table} row={row} />
                                    </Flex>
                           </Stack>
                  ),
                  renderEditRowModalContent: ({ table, row, internalEditComponents }) => (
                           <Stack>
                                    <Title order={3}>Edit Team</Title>
                                    {internalEditComponents}
                                    <Flex justify="flex-end" mt="xl">
                                             <MRT_EditActionButtons variant="text" table={table} row={row} />
                                    </Flex>
                           </Stack>
                  ),
                  renderRowActions: ({ row, table }) => (
                           <Flex gap="md">
                                    <Tooltip label="Show">
                                             <ActionIcon onClick={() => navigate(`/teams/${row.original.id}`)}>
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
                           <Button
                                    onClick={() => {
                                             table.setCreatingRow(true); //simplest way to open the create row modal with no default values
                                             //or you can pass in a row object to set default values with the `createRow` helper function
                                             // table.setCreatingRow(
                                             //   createRow(table, {
                                             //     //optionally pass in default values for the new row, useful for nested data or other complex scenarios
                                             //   }),
                                             // );
                                    }}
                           >
                                    Create New Team
                           </Button>
                  ),
                  state: {
                           isLoading: isLoadingteams,
                           isSaving: isCreatingteam || isUpdatingteam || isDeletingteam,
                           showAlertBanner: isLoadingteamsError,
                           showProgressBars: isFetchingteams,
                  },
         });

         return <MantineReactTable table={table} />;
};

const validateRequired = (value: string) => !!value.length;

function validateTeam(Team: Team) {
         return {
                  name: !validateRequired(Team.name)
                           ? 'First Name is Required'
                           : '',

         };
}

export default PaginationTable;