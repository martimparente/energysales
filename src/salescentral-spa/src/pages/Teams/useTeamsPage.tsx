import {useCreateTeam, useDeleteTeam, useGetTeams, useUpdateTeam} from '../../services/TeamsService';
import {useNavigate} from "react-router-dom"
import {CreateTeamInputModel, Team, UpdateTeamInputModel} from "../../services/models/TeamModel";
import {Column} from '../../components/GenericTable';

export function useTeamsPage() {

    const navigate = useNavigate();

    const {data: teams, error, isFetching} = useGetTeams();
    const {mutateAsync: createTeam} = useCreateTeam();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();

    const columns: Column<Team>[] = [
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
            accessor: 'location',
            header: 'District',
            sortable: true,
        },
    ];

    return {
        columns,
        teams,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input),
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input),
        deleteTeam: async (team: Team) => await deleteTeam(team.id),
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        isFetching,
        error,
    }
}