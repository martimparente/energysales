import { GenericTable } from '../../components/GenericTable';
import { Team } from '../../interfaces/Teams';
import { useTeams } from './useTeams';

export function TeamsPage() {
  const {
    columns,
    data,
    createTeam,
    handleCreateTeamForm,
    updateTeam,
    handleUpdateTeamForm,
    deleteTeam,
    isFetching,
    error } = useTeams();

  return (
    <GenericTable<Team>
      columns={columns}
      data={data}
      createResource={createTeam}
      handleCreateResourceForm={handleCreateTeamForm}
      updateResource={updateTeam}
      handleUpdateResourceForm={handleUpdateTeamForm}
      deleteResource={deleteTeam}
      isFetching={isFetching}
      error={error}
    />
  );
}