import {useTeamPage} from './useTeamPage'
import {User} from '../../../services/models/UserModel.tsx'
import {Box, Button, LoadingOverlay, Select, Table} from '@mantine/core'

export function TeamPage() {
    const {
        teamDetails,
        isLoadingTeamDetails,
        availableSellers,
        handleSelectSellerChange,
        handleAddSelectSellerToTeam,
        handleDeleteSellerFromTeam,
    } = useTeamPage()


    return (
        <Box pos="relative">
            <LoadingOverlay visible={isLoadingTeamDetails}/>
            <h1>{teamDetails?.team.name}</h1>
            <p>Name = {teamDetails?.team.name}</p>
            <p>Location = {teamDetails?.team.location?.district}</p>
            <p>Manager = {teamDetails?.team.manager?.toString()}</p>

            <Select
                label="Select seller to add to Team"
                placeholder="Search for seller"
                limit={5}
                data={availableSellers?.map((member: User) => ({
                    value: member.id,
                    label: member.name
                })) || []}
                onChange={(value) => handleSelectSellerChange(value!)}
            />

            <Button onClick={handleAddSelectSellerToTeam} color="orange" mb="md">Add Seller to Team</Button>

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
                        <Table.Tr>
                            <Table.Td>{member.name}</Table.Td>
                            <Table.Td>{member.surname}</Table.Td>
                            <Table.Td>{member.email}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => handleDeleteSellerFromTeam(member.id)} color={"red"}>
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
