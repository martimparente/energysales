import {useDeleteTeam, useGetManagerCandidates, useGetTeams} from '../../services/TeamsService'
import {useNavigate} from 'react-router-dom'
import {Team} from '../../services/models/TeamModel'
import {useState} from 'react'
import {ManagerInfo} from '../../services/models/UserModel.tsx'

export function useTeamsPage() {
    const navigate = useNavigate()

    const {data: teams, error: fetchError, isFetching} = useGetTeams()
    const {mutateAsync: deleteTeam} = useDeleteTeam()
    const {data: managersCandidates} = useGetManagerCandidates()
    const [error, setError] = useState<string | null>()

    if (fetchError && !error) {
        setError(fetchError.message)
    }

    /*    const mappedManagersCandidates: Record<string, { email: string }> = managersCandidates?.reduce((acc, manager) => {
            const fullName = `${manager.name} ${manager.surname}`;
            acc[fullName] = {
                email: manager.email,
            };
            return acc;
        }, {} as Record<string, { email: string }>) || {};*/

    const mappedManagersCandidates = managersCandidates?.map((manager: ManagerInfo) => `${manager.name} ${manager.surname}`)

    return {
        teams,
        deleteTeam: async (team: Team) => await deleteTeam(team.id).catch((e) => setError(e.message)),
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        managersCandidates,
        mappedManagersCandidates,
        onCreateTeamButtonClick: () => navigate(`/teams/create`),
        isFetching,
        error
    }
}
