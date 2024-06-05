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
        isFetching,
        error
    } = useTeamsPage();

    return (
        <div>
            <h1>Teams</h1>
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
                                <Button onClick={() => onShowClickHandler(team)}>Show</Button>
                                <Button color={"green"}>Edit</Button>
                                <Button onClick={() => deleteTeam(team)} color={"red"}><IconTrash stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}