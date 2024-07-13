import {useTeamPage} from './useTeamPage'
import {Seller, User} from '../../../services/models/UserModel.tsx'
import {Text, Box, Button, Group, LoadingOverlay, Table, TextInput} from '@mantine/core'
import {ReactSearchAutocomplete} from 'react-search-autocomplete'
import {Controller, useForm} from 'react-hook-form'
import {Simulate} from "react-dom/test-utils";
import reset = Simulate.reset;
import {useState} from "react";

export function TeamPage() {
    const {
        teamDetails,
        availableSellers,
        handleOnDeleteTeam,
        handleOnSellerSearch,
        handleOnSellerSelect,
        handleOnAddSellerToTeam,
        handleOnDeleteSellerFromTeam,
        handleUpdateTeam,
        isPending,
        error,
    } = useTeamPage()

    const {control, handleSubmit} = useForm({
        defaultValues: {
            name: teamDetails?.team.name || '',
            location: teamDetails?.team.location?.district || '',
            manager: teamDetails?.team.manager?.toString() || ''
        }
    });

    const [isEditing, setIsEditing] = useState(false);

    const handleEditClick = () => {
        setIsEditing(true);
    };

    const handleCancelClick = () => {
        setIsEditing(false);
        reset(); // Reset form to initial values
    };

    const formatResult = (item: Seller) => {
        return (
            <Group gap="sm">
                {/*<Avatar src={item.image} size={36} radius="xl" />*/}
                <div>
                    <Text size="sm">{item.name}</Text>
                    <Text size="xs" opacity={0.5}>{item.email}</Text>
                </div>
            </Group>
        )
    }

    return (
        <Box pos="relative">
            <LoadingOverlay visible={isPending}/>
                <LoadingOverlay visible={isPending} />
                <h1>{isEditing ? 'Edit Team Details' : 'Team Details'}</h1>

                {isEditing ? (
                    <form onSubmit={handleSubmit(handleUpdateTeam)}>
                        <Controller
                            name="name"
                            control={control}
                            render={({ field }) => (
                                <TextInput
                                    label="Name"
                                    placeholder="Enter team name"
                                    {...field}
                                    required
                                />
                            )}
                        />
                        <Controller
                            name="location"
                            control={control}
                            render={({ field }) => (
                                <TextInput
                                    label="Location"
                                    placeholder="Enter team location"
                                    {...field}
                                    required
                                />
                            )}
                        />
                        <Controller
                            name="manager"
                            control={control}
                            render={({ field }) => (
                                <TextInput
                                    label="Manager"
                                    placeholder="Enter manager name"
                                    {...field}
                                    required
                                />
                            )}
                        />
                        <Group mt="md">
                            <Button type="submit">Update Team</Button>
                            <Button variant="outline" onClick={handleCancelClick}>Cancel</Button>
                        </Group>
                    </form>
                ) : (
                    <div>
                        <p>Name = {teamDetails?.team.name}</p>
                        <p>Location = {teamDetails?.team.location?.district}</p>
                        <p>Manager = {teamDetails?.team.manager?.toString()}</p>
                        <Button onClick={handleEditClick} mb="md">Edit</Button>
                    </div>
                )}

            {error && <p>{error}</p>}

            <Button onClick={handleOnAddSellerToTeam} color="orange" mb="md">Add Seller to Team</Button>
            <Button onClick={handleOnDeleteTeam} color="red" mb="md">Delete Team</Button>

            <ReactSearchAutocomplete<Seller>
                items={availableSellers}
                onSearch={handleOnSellerSearch}
                onSelect={handleOnSellerSelect}
                formatResult={formatResult}
            />

            <Table>
                <Table.Thead>
                    <Table.Tr>
                        <Table.Th>Name</Table.Th>
                        <Table.Th>Surname</Table.Th>
                        <Table.Th>Email</Table.Th>
                        <Table.Th>Total Sales</Table.Th>
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {teamDetails?.members?.map((member: User) => (
                        <Table.Tr key={member.id.toString()}>
                            <Table.Td>{member.name}</Table.Td>
                            <Table.Td>{member.surname}</Table.Td>
                            <Table.Td>{member.email}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => handleOnDeleteSellerFromTeam(member.id)} color={"red"}>
                                    Remove from Team
                                </Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </Box>
    )
}