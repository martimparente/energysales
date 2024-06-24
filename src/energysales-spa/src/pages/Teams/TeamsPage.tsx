import {useTeamsPage} from './useTeamsPage.tsx';
import {CreateTeamInputModel, Team} from '../../services/models/TeamModel';
import {Autocomplete, AutocompleteProps, Button, Group, Modal, Table, Text, TextInput} from "@mantine/core";
import {IconTrash} from '@tabler/icons-react';
import React, {useState} from "react";

export function TeamsPage() {
    const {
        columns,
        teams,
        createTeam,
        // updateTeam,
        deleteTeam,
        openCreateModal,
        onEditButtonHandler,
        onShowClickHandler,
        closeCreateModal,
        closeEditModal,
        managersCandidates,
        mappedManagersCandidates,
        isCreating,
        isEditing,
        isFetching,
        error,
    } = useTeamsPage();

    const [teamName, setTeamName] = useState('');
    const [district, setDistrict] = useState('');
    const [manager, setManager] = useState('');

    const renderAutocompleteOption: AutocompleteProps['renderOption'] = ({option}) => (
        <Group gap="sm">
            {/*<Avatar src={managersCandidates[option.value].image} size={36} radius="xl" />*/}
            <div>
                <Text size="sm">{option.value}</Text>
                {<Text size="xs" opacity={0.5}>
                    {getEmailByManagerName(option.value)}
                </Text>}
            </div>
        </Group>
    );

    // Function to get the email of the manager based on the full name
    const getEmailByManagerName = (name: string) => {
        const manager = managersCandidates?.find((manager) => `${manager.name} ${manager.surname}` === name);
        return manager ? manager.email : '';
    };

    const handleCreateTeam = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const input: CreateTeamInputModel = {
            name: teamName,
            location: {district: district},
            managerId: manager,
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
                    <form onSubmit={(e) => handleCreateTeam(e)}>
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
                        <Autocomplete
                            data={mappedManagersCandidates}
                            renderOption={renderAutocompleteOption}
                            maxDropdownHeight={300}
                            label="Manager"
                            placeholder="Search for employee"
                            onChange={(managerLabel) => {
                                const id = managersCandidates?.find((manager) => managerLabel === `${manager.name} ${manager.surname}`)?.id
                                setManager(id)
                            }
                            }
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