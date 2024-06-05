import {useCreateTeam, useDeleteTeam, useGetTeams, useUpdateTeam} from '../../services/TeamsService';
import {useNavigate} from "react-router-dom"
import {CreateTeamInputModel, Team, UpdateTeamInputModel} from "../../services/models/TeamModel";
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useTeamsPage() {

    const navigate = useNavigate();

    const {data: teams, error: fetchError, isFetching} = useGetTeams();
    const {mutateAsync: createTeam} = useCreateTeam();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();

    const [error, setError] = useState<string | null>()

    if (fetchError && !error) {
        setError(fetchError.message);
    }

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
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(e => setError(e)),
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input).catch(e => setError(e)),
        deleteTeam: async (team: Team) => await deleteTeam(team.id).catch(e => setError(e)),
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        isFetching,
        error,
    }
}