import {useTeamsPage} from './useTeamsPage.tsx';
import {Team} from '../../services/models/TeamModel';
import {Button, Table} from "@mantine/core";
import {IconTrash} from '@tabler/icons-react';

export function TeamsPage() {
    const {
        columns,
        teams,
        createTeam,
        updateTeam,
        deleteTeam,
        onShowClickHandler,
        managersCandidates,
        mappedManagersCandidates,
        onCreateTeamButtonClick,
        isFetching,
        error,
    } = useTeamsPage();


    // Function to get the email of the manager based on the full name
    const getEmailByManagerName = (name: string) => {
        const manager = managersCandidates?.find((manager) => `${manager.name} ${manager.surname}` === name);
        return manager ? manager.email : '';
    };


    return (
        <div>
            <h1>Teams</h1>
            <Button onClick={() => onCreateTeamButtonClick()} color={"green"}>Create Service</Button>

            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {teams?.map((team: Team) => (
                        <Table.Tr key={team.id}>
                            <Table.Td>{team.name}</Table.Td>
                            <Table.Td>{team.location.district}</Table.Td>
                            <Table.Td>{team.manager}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowClickHandler(team)} color={"green"}>Show</Button>
                                <Button onClick={console.log} color={"orange"}>Edit</Button>
                                <Button onClick={() => deleteTeam(team)} color={"red"}><IconTrash stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}