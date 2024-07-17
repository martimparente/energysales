import {useTeamPage} from './useTeamPage'
import {Seller, User} from '../../../services/models/UserModel.tsx'
import {Box, Button, Group, LoadingOverlay, Table, Text, TextInput} from '@mantine/core'
import {ReactSearchAutocomplete} from 'react-search-autocomplete'
import {Controller, useForm} from 'react-hook-form'
import {Simulate} from "react-dom/test-utils";
import {useState} from "react";
import {Service} from "../../../services/models/ServiceModel.tsx";
import {IconTrash} from "@tabler/icons-react";
import {Column} from "../../../components/GenericTable.tsx";
import reset = Simulate.reset;

export function TeamPage() {
    const {
        teamDetails,
        availableSellers,
        availableServices,
        handleOnDeleteTeam,
        handleOnSellerSearch,
        handleOnSellerSelect,
        handleOnAddSellerToTeam,
        handleOnDeleteSellerFromTeam,
        handleUpdateTeam,
        handleOnShowService,
        handleOnAddServiceToTeam,
        handleOnDeleteServiceFromTeam,
        handleOnServiceSelect,
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

    const columns: Column[] = [
        {accessor: 'name', header: 'Name', sortable: true},
        {accessor: 'cycleName', header: 'Cycle Name', sortable: true},
        {accessor: 'cycleType', header: 'Cycle Type', sortable: true},
        {accessor: 'description', header: 'Description', sortable: true},
        {accessor: 'actions', header: 'Action', sortable: false},
    ];

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
            <LoadingOverlay visible={isPending}/>
            <h1>{isEditing ? 'Edit Team Details' : 'Team Details'}</h1>

            {isEditing ? (
                <form onSubmit={handleSubmit(handleUpdateTeam)}>
                    <Controller
                        name="name"
                        control={control}
                        render={({field}) => (
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
                        render={({field}) => (
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
                        render={({field}) => (
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
                    <Button onClick={handleOnDeleteTeam} color="red" mb="md">Delete Team</Button>
                </div>
            )}

            {error && <p>{error}</p>}

            <Button onClick={handleOnAddSellerToTeam} color="orange" mb="md">Add Seller to Team</Button>

            <ReactSearchAutocomplete<Seller>
                items={availableSellers!}
                onSearch={handleOnSellerSearch}
                onSelect={handleOnSellerSelect}
                formatResult={formatResult}
            />


            <h1>Sellers</h1>
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

            <h1>Services</h1>

            <Button onClick={handleOnAddServiceToTeam} color="orange" mb="md">Add Service to Team</Button>

            <ReactSearchAutocomplete<Service>
                items={availableServices!}
                onSelect={handleOnServiceSelect}
                formatResult={formatResult}
            />

            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {teamDetails?.services.map((service: Service) => (
                        <Table.Tr key={service.id}>
                            <Table.Td>{service.name}</Table.Td>
                            <Table.Td>{service.cycleName}</Table.Td>
                            <Table.Td>{service.cycleType}</Table.Td>
                            <Table.Td>{service.description}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => handleOnShowService(service.id)} color={"orange"}>Show</Button>
                                <Button onClick={() => handleOnDeleteServiceFromTeam(service.id)}
                                        color={"red"}><IconTrash stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </Box>
    )
}