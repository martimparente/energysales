import {
    useCreateTeam,
    useDeleteTeam,
    useGetManagerCandidates,
    useGetTeams,
    useUpdateTeam
} from '../../services/TeamsService';
import {useNavigate} from "react-router-dom"
import {CreateTeamInputModel, Team, UpdateTeamInputModel} from "../../services/models/TeamModel";
import {useState} from "react";
import {Column} from "../../components/GenericTable.tsx";
import {ManagerInfo} from "../../services/models/UserModel.tsx";

export function useTeamsPage() {

    const navigate = useNavigate();

    const {data: teams, error: fetchError, isFetching} = useGetTeams();
    const {mutateAsync: createTeam} = useCreateTeam();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();
    const {data: managersCandidates} = useGetManagerCandidates();
    const [error, setError] = useState<string | null>()

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    const columns: Column[] = [
        {accessor: 'name', header: 'Name', sortable: true,},
        {accessor: 'location', header: 'District', sortable: true,},
        {accessor: 'manager', header: 'Manager', sortable: true,},
        {accessor: 'actions', header: 'Actions', sortable: false,},
    ];

    /*    const mappedManagersCandidates: Record<string, { email: string }> = managersCandidates?.reduce((acc, manager) => {
            const fullName = `${manager.name} ${manager.surname}`;
            acc[fullName] = {
                email: manager.email,
            };
            return acc;
        }, {} as Record<string, { email: string }>) || {};*/

    const mappedManagersCandidates =
        managersCandidates?.map((manager: ManagerInfo) => `${manager.name} ${manager.surname}`);


    return {
        columns,
        teams,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(e => setError(e.message)),
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input).catch(e => setError(e.message)),
        deleteTeam: async (team: Team) => await deleteTeam(team.id).catch(e => setError(e.message)),
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        managersCandidates,
        mappedManagersCandidates,
        onCreateTeamButtonClick: () => navigate(`/teams/create`),
        isFetching,
        error,
    }
}