import {useTeamsPage} from './useTeamsPage.tsx';
import {CreateTeamInputModel, Team} from '../../services/models/TeamModel';
import {Button, Group, Modal, Table, TextInput} from "@mantine/core";
import {IconTrash} from '@tabler/icons-react';
import {useState} from "react";

export function TeamsPage() {
    const {
        columns,
        teams,
        createTeam,
        updateTeam,
        deleteTeam,
        openCreateModal,
        onEditButtonHandler,
        onShowClickHandler,
        closeCreateModal,
        closeEditModal,
        isCreating,
        isEditing,
        isFetching,
        error,
    } = useTeamsPage();

    const [teamName, setTeamName] = useState('');
    const [district, setDistrict] = useState('');
    const [manager, setManager] = useState('');

    const handleCreateTeam = async (e) => {
        e.preventDefault();
        const input: CreateTeamInputModel = {
            name: teamName,
            location: {district: district},
            manager: manager,
        };
        await createTeam(input);
        closeCreateModal();
        setTeamName('');
        setDistrict('');
        setManager('');
    };


    return (
        <div>
            <h1>Teams</h1>

            <Modal opened={isCreating} onClose={closeCreateModal} title="Create Team">
                {
                    <form onSubmit={handleCreateTeam}>
                        <TextInput
                            label="Team Name"
                            placeholder="Enter team name"
                            value={teamName}
                            onChange={(e) => setTeamName(e.currentTarget.value)}
                            required
                        />
                        <TextInput
                            label="District"
                            placeholder="Enter district"
                            value={district}
                            onChange={(e) => setDistrict(e.currentTarget.value)}
                            required
                        />
                        <TextInput
                            label="Manager ID"
                            placeholder="Enter manager ID"
                            value={manager}
                            onChange={(e) => setManager(e.currentTarget.value)}
                            required
                        />
                        <Group mt="md">
                            <Button type="submit">Create</Button>
                        </Group>
                    </form>}
            </Modal>

            <Modal opened={isEditing} onClose={closeEditModal} title="Update Team">
                {/*  Your form for updating a team
                <form onSubmit={(e) => {
                    e.preventDefault();
                    const input = {}; // Get data from your form
                    updateTeam(input);
                    closeEditModal();
                }}>
                     Form fields
                    <Button type="submit">Update</Button>
                </form>*/}
            </Modal>

            {isFetching && <p>Loading...</p>}
            {error && <p>{error}</p>}

            <Button onClick={openCreateModal} color={"orange"}>Create Team</Button>
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
                                <Button onClick={() => onEditButtonHandler()} color={"orange"}>Edit</Button>
                                <Button onClick={() => deleteTeam(team)} color={"red"}><IconTrash stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}