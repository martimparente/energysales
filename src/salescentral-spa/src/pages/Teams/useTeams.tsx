import { useGetTeams, useCreateTeam, useUpdateTeam, useDeleteTeam } from '../../services/TeamsService';
import { useNavigate } from "react-router-dom"
import { CreateTeamInputModel, UpdateTeamInputModel } from "../../services/models/TeamModel";

export function useTeams() {

  const navigate = useNavigate();

  const { data, error, isFetching, isLoading } = useGetTeams();
  const { mutateAsync: createTeam } = useCreateTeam();
  const { mutateAsync: updateTeam } = useUpdateTeam();
  const { mutateAsync: deleteTeam } = useDeleteTeam();

  const columns = [
    {
      accessor: 'name',
      header: 'Name',
      sortable: true,
    },
    {
      accessor: 'manager',
      header: 'Manager',
      sortable: true,
    },
    {
      accessor: 'location.district',
      header: 'District',
      sortable: true,
    },
  ];

  return {
    columns,
    data,
    isLoading,
    createTeam: async (input: CreateTeamInputModel) => await createTeam(input),
    handleCreateTeamForm: () => { },
    updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input),
    handleUpdateTeamForm: () => { },
    deleteTeam: async (id: string) => await deleteTeam(id),
    isFetching,
    error,
  }
}