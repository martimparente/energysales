import {useCreateTeam, useDeleteTeam, useGetTeams, useUpdateTeam} from '../../services/TeamsService';
import {useNavigate} from "react-router-dom"
import {CreateTeamInputModel, Team, UpdateTeamInputModel} from "../../services/models/TeamModel";
import {useState} from "react";
import {Column} from "../../components/GenericTable.tsx";
import {useDisclosure} from "@mantine/hooks";

export function useTeamsPage() {

    const navigate = useNavigate();

    const {data: teams, error: fetchError, isFetching} = useGetTeams();
    const {mutateAsync: createTeam} = useCreateTeam();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();

    const [isCreating, {open: openCreateModal, close: closeCreateModal}] = useDisclosure(false);
    const [isEditing, {open: openEditModal, close: closeEditModal}] = useDisclosure(false);

    const [error, setError] = useState<string | null>()

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    const columns: Column[] = [
        {
            accessor: 'name',
            header: 'Name',
            sortable: true,
        },
        {
            accessor: 'location',
            header: 'District',
            sortable: true,
        },
        {
            accessor: 'manager',
            header: 'Manager',
            sortable: true,
        },
    ];

    return {
        columns,
        teams,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(e => setError(e.message)),
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input).catch(e => setError(e.message)),
        deleteTeam: async (team: Team) => await deleteTeam(team.id).catch(e => setError(e.message)),
        openCreateModal,
        onEditButtonHandler: (team: Team) => {
            // Optionally set the editing team here
            openEditModal();
        },
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        closeCreateModal,
        closeEditModal,
        isCreating,
        isEditing,
        isFetching,
        error,
    }
}