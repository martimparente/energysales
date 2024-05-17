import { useMemo, useState } from 'react';
import {
         MRT_EditActionButtons,
         MantineReactTable,
         // createRow,
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
import { IconEdit, IconTrash, IconEye} from '@tabler/icons-react';
import {
         useMutation,
         useQuery,
         useQueryClient,
} from '@tanstack/react-query';
import { notifications } from '@mantine/notifications';
import { useNavigate } from "react-router-dom";


interface Team {
         id: string;
         name: string;
         manager: string;
         location: Location;
}
interface Location {
         district: string;
}

const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU5MDM3MjV9.1VCdT-gl8V1T5b1tGoNENMUtREhV6nSz1-pUK6GplEk"
const Example = () => {
         const [validationErrors, setValidationErrors] = useState<
                  Record<string, string | undefined>
         >({});

         const navigate = useNavigate();

         const columns = useMemo<MRT_ColumnDef<Team>[]>(
                  () => [
                           {
                                    accessorKey: 'id',
                                    header: 'Id',
                                    enableEditing: false,
                                    size: 80,
                           },
                           {
                                    accessorKey: 'name',
                                    header: 'Name',
                                    mantineEditTextInputProps: {
                                             type: 'text',
                                             required: true,
                                             error: validationErrors?.firstName,
                                             //remove any previous validation errors when team focuses on the input
                                             onFocus: () =>
                                                      setValidationErrors({
                                                               ...validationErrors,
                                                               name: undefined,
                                                      }),
                                             //optionally add validation checking for onBlur or onChange
                                    },
                           },
                           {
                                    accessorKey: 'district',
                                    header: 'Location',
                                    mantineEditTextInputProps: {
                                             type: 'text',
                                             required: true,

                                             //remove any previous validation errors when team focuses on the input
                                             onFocus: () =>
                                                      setValidationErrors({
                                                               ...validationErrors,
                                                               name: undefined,
                                                      }),
                                    },
                           },
                           {
                                    accessorKey: 'manager',
                                    header: 'Manager',
                                    mantineEditTextInputProps: {
                                             type: 'number',
                                             required: true,
                                             error: validationErrors?.manager,
                                             //remove any previous validation errors when team focuses on the input
                                             onFocus: () =>
                                                      setValidationErrors({
                                                               ...validationErrors,
                                                               email: undefined,
                                                      }),
                                    },
                           },

                  ],
                  [validationErrors],
         );

         //call CREATE hook
         const { mutateAsync: createTeam, isLoading: isCreatingteam } =
                  useCreateTeam();
         //call READ hook
         const {
                  data: fetchedteams = [],
                  isError: isLoadingteamsError,
                  isFetching: isFetchingteams,
                  isLoading: isLoadingteams,
         } = useGetTeams();
         //call UPDATE hook
         const { mutateAsync: updateTeam, isLoading: isUpdatingteam } =
                  useUpdateTeam();
         //call DELETE hook
         const { mutateAsync: deleteTeam, isLoading: isDeletingteam } =
                  useDeleteTeam();

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
                           onConfirm: () => deleteTeam(row.original.id),
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
                                             <ActionIcon onClick={() => navigate("/")}>
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

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
//CREATE hook (post new team to api)
function useCreateTeam() {
         return useMutation({
                  mutationFn: async (team: Team) => {
                           console.log(team)
                           const team1 = {
                                    name: team.name,
                                    manager: team.manager,
                                    location: { district: team.district }
                           }
                           console.log(team1);
                           return fetch(`http://localhost:8080/api/teams`, {
                                    method: 'POST',
                                    headers: {
                                             'Content-Type': 'application/json',
                                             'Authorization': "Bearer " + token,
                                    },
                                    body: JSON.stringify(team1),

                           }).then((res) => res.json())
                  },
                  //client side optimistic update
                  onMutate: (newTeamInfo: Team) => {
                  },
                  // onSettled: () => queryClient.invalidateQueries({ queryKey: ['teams'] }), //refetch teams after mutation, disabled for demo
         });
}

//READ hook (get teams from api)
function useGetTeams() {
         console.log('useGetTeams');
         return useQuery<Team[]>({
                  queryKey: ['teams'],
                  queryFn: () => fetch(`http://localhost:8080/api/teams?lastKeySeen=0`, {
                           headers: {
                                    'Accept': 'application/json',
                                    'Authorization': "Bearer " + token
                           }
                  }).then((res) => res.json()),
         }
         )
}

//UPDATE hook (put team in api)
function useUpdateTeam() {
         const queryClient = useQueryClient();
         return useMutation({
                  mutationFn: (newTeamInfo: Team) => fetch(`http://localhost:8080/api/teams/${newTeamInfo.id}`, {
                           method: 'PUT',
                           headers: {
                                    'Accept': 'application/json',
                                    'Authorization': "Bearer " + token
                           }
                  }),
                  //client side optimistic update
                  onMutate: (newTeamInfo: Team) => {
                           queryClient.setQueryData(
                                    ['teamsupd'],
                                    (prevteams: any) =>
                                             prevteams?.map((prevTeam: Team) =>
                                                      prevTeam.id === newTeamInfo.id ? newTeamInfo : prevTeam,
                                             ),
                           );
                  },
                  // onSettled: () => queryClient.invalidateQueries({ queryKey: ['teams'] }), //refetch teams after mutation, disabled for demo
         });
}

//DELETE hook (delete team in api)
function useDeleteTeam() {
         console.log('useDeleteTeam');
         const queryClient = useQueryClient();
         return useMutation({
                  mutationFn: (teamId: string) => fetch(`http://localhost:8080/api/teams/${teamId}`, {
                           method: 'DELETE',
                           headers: {
                                    'Content-Type': 'application/json',
                                    'Authorization': "Bearer " + token,

                           }
                  }),
                  //client side optimistic update
                  onMutate: (teamId: string) => {
                  },
                  // onSettled: () => queryClient.invalidateQueries({ queryKey: ['teams'] }), //refetch teams after mutation, disabled for demo
         });
}


const validateRequired = (value: string) => !!value.length;

function validateTeam(Team: Team) {
         return {
                  name: !validateRequired(Team.name)
                           ? 'First Name is Required'
                           : '',

         };
}

export default Example;