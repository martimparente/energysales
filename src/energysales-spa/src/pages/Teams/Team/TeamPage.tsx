import React, {useState} from 'react'
import {useTeamPage} from './useTeamPage'
import {
    Avatar,
    Box,
    Button,
    Card,
    Group,
    LoadingOverlay,
    Space,
    Stack,
    Table,
    Text,
    Title,
    Tooltip
} from '@mantine/core'
import {IconMapPin, IconPlus, IconTrash, IconUser} from '@tabler/icons-react'
import {ApiUris} from '../../../services/ApiUris.tsx'
import {Link} from 'react-router-dom'
import {EditTeamDrawer} from '../../../components/Drawers/EditTeamDrawer.tsx'

export function TeamPage() {
    const [drawerOpened, setDrawerOpened] = useState(false)

    const {
        teamDetails,
        handleOnDeleteSellerFromTeam,
        handleOnShowService,
        handleOnDeleteServiceFromTeam,
        isFetching,
        avatarUploadModal,
        addTeamSellerModal,
        addTeamServiceModal,
        handleUpdateTeam,
        confirmDeleteModal
    } = useTeamPage()

    const columns = [
        {accessor: 'name', header: 'Name', sortable: true},
        {accessor: 'cycleName', header: 'Cycle Name', sortable: true},
        {accessor: 'cycleType', header: 'Cycle Type', sortable: true},
        {accessor: 'description', header: 'Description', sortable: true},
        {accessor: 'actions', header: 'Action', sortable: false}
    ]

    return (
        <Stack pos='relative'>
            <LoadingOverlay
                visible={isFetching || !teamDetails}
                zIndex={1000}
                overlayProps={{radius: 'sm', blur: 100}}
                loaderProps={{color: 'green', size: 40}}
            />

            <Card shadow='sm' padding='lg' radius='md' withBorder>
                <Group gap='xl'>
                    <Tooltip label='Edit Avatar' withArrow>
                        <Avatar
                            src={ApiUris.STATIC_RESOURCES_URL + teamDetails?.team.avatarPath}
                            size={120}
                            onClick={avatarUploadModal}
                            style={{cursor: 'pointer'}}
                            color='yellow'
                        />
                    </Tooltip>
                    <div>
                        <Title order={2} style={{marginBottom: '8px'}}>
                            {teamDetails?.team.name}
                        </Title>
                        <Group align='center' style={{marginBottom: '8px'}}>
                            <IconMapPin size={20}/>
                            <Text size='md' c='dimmed'>
                                {teamDetails?.team.location?.district}
                            </Text>
                        </Group>
                        <Group align='center'>
                            <IconUser size={20}/>
                            <Text size='md' c='dimmed'>
                                {teamDetails?.team.manager != null ? (
                                    <Link
                                        to={`/manager/${teamDetails?.team.manager}`}
                                        style={{
                                            textDecoration: 'none',
                                            color: '#1c7ed6'
                                        }}
                                    >
                                        {teamDetails?.team.manager}
                                    </Link>
                                ) : (
                                    'No Manager'
                                )}
                            </Text>
                        </Group>
                    </div>
                </Group>
            </Card>

            <Card shadow='sm' padding='lg' radius='md' withBorder>
                <Group mb='lg' justify='space-between'>
                    <Title order={2}>Sellers</Title>
                    <Button onClick={addTeamSellerModal} color='blue'>
                        <IconPlus size={16}/>
                    </Button>
                </Group>

                {teamDetails?.members?.length > 0 ? (
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
                            {teamDetails?.members?.map((member) => (
                                <Table.Tr key={member.id.toString()}>
                                    <Table.Td>{member.name}</Table.Td>
                                    <Table.Td>{member.surname}</Table.Td>
                                    <Table.Td>{member.email}</Table.Td>
                                    <Table.Td>
                                        <Button onClick={() => handleOnDeleteSellerFromTeam(member.id)} color={'red'}>
                                            Remove from Team
                                        </Button>
                                    </Table.Td>
                                </Table.Tr>
                            ))}
                        </Table.Tbody>
                    </Table>
                ) : (
                    <Box
                        style={{
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            height: '100%'
                        }}
                    >
                        <Text c='dimmed'>No sellers</Text>
                    </Box>
                )}
            </Card>

            <Card shadow='sm' padding='lg' radius='md' withBorder>
                <Group mb='lg' justify='space-between'>
                    <Title order={2}>Services</Title>
                    <Button onClick={addTeamServiceModal} color='blue'>
                        <IconPlus size={16}/>
                    </Button>
                </Group>
                {teamDetails?.services?.length > 0 ? (
                    <Table>
                        <Table.Thead>
                            <Table.Tr>
                                {columns.map((column) => (
                                    <Table.Th key={column.header}>{column.header}</Table.Th>
                                ))}
                            </Table.Tr>
                        </Table.Thead>
                        <Table.Tbody>
                            {teamDetails?.services.map((service) => (
                                <Table.Tr key={service.id}>
                                    <Table.Td>{service.name}</Table.Td>
                                    <Table.Td>{service.cycleName}</Table.Td>
                                    <Table.Td>{service.cycleType}</Table.Td>
                                    <Table.Td>{service.description}</Table.Td>
                                    <Table.Td>
                                        <Button onClick={() => handleOnShowService(service.id)} color={'orange'}>
                                            Show
                                        </Button>
                                        <Button onClick={() => handleOnDeleteServiceFromTeam(service.id)} color={'red'}>
                                            <IconTrash stroke={2}/>
                                        </Button>
                                    </Table.Td>
                                </Table.Tr>
                            ))}
                        </Table.Tbody>
                    </Table>
                ) : (
                    <Box
                        style={{
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            height: '100%'
                        }}
                    >
                        <Text c='dimmed'>No services</Text>
                    </Box>
                )}
            </Card>

            <Box mt='md' style={{display: 'flex', justifyContent: 'center', width: '100%'}}>
                <Button onClick={() => setDrawerOpened(true)} color='blue' mb='md'>
                    Edit Team
                </Button>
                <Space w='md'/>
                <Button onClick={confirmDeleteModal} color='red' mb='md'>
                    Delete Team
                </Button>
            </Box>

            <EditTeamDrawer
                opened={drawerOpened}
                onClose={() => setDrawerOpened(false)}
                onSubmit={handleUpdateTeam}
                initialData={{
                    name: teamDetails?.team.name,
                    location: teamDetails?.team.location,
                    managerId: 2
                }}
            />
        </Stack>
    )
}
