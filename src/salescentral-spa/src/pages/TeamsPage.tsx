import { Button } from '@mantine/core';
import PaginationTable from '../components/PaginationTable';
import { showNotification } from "@mantine/notifications";
import { useGetTeams, useCreateTeam, useUpdateTeam, useDeleteTeam } from '../services/TeamsService';
import { Team } from "../interfaces/Teams";

const columns = [
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
    },
  },
  {
    accessorKey: 'district',
    header: 'Location',
    mantineEditTextInputProps: {
      type: 'text',
      required: true,
    },
  },
  {
    accessorKey: 'manager',
    header: 'Manager',
    mantineEditTextInputProps: {
      type: 'number',
      required: true,
    },
  },
];

const validateTeam = (team: Team) => {
  return {
    name: !team.name ? 'Name is required' : undefined,
    district: !team.district ? 'Location is required' : undefined,
    manager: !team.manager ? 'Manager is required' : undefined,
  };
};

export function TeamsPage() {
  const {
    data: teams = [],
    isError,
    isFetching,
    isLoading,
  } = useGetTeams();
  const { mutateAsync: createTeam } = useCreateTeam();
  const { mutateAsync: updateTeam } = useUpdateTeam();
  const { mutateAsync: deleteTeam } = useDeleteTeam();

  return (
    <div>
      <h1>Teams</h1>
      <Button
        variant="outline"
        onClick={() =>
          showNotification({
            title: "Default notification",
            message: "Hey there, your code is awesome! 🤥"
          })
        }
      >NOTIFICATION</Button>
      <PaginationTable<Team>
        columnHeaders={columns}
        data={teams}
        createItem={createTeam}
        updateItem={updateTeam}
        deleteItem={deleteTeam}
        validateItem={validateTeam}
        isLoading={isLoading}
        isFetching={isFetching}
        isError={isError}
        resourceName='teams'
      />
    </div>
  );
}