import {useTeamPage} from './useTeamPage'
import {Seller} from '../../../services/models/SellersModel.tsx'
import {Button, Select, Table} from '@mantine/core'

export function TeamPage() {
    const {
        teamDetails,
        availableSellers,
        isFetching,
        handleSelectSellerChange,
        handleAddSelectSellerToTeam
    } = useTeamPage()

    if (isFetching) return <p>loading</p>

    return (
        <div>
            <h1>Team</h1>

            <p>Name = {teamDetails?.team.name}</p>
            <p>Location = {teamDetails?.team.location?.district}</p>

            <Select
                label="Select seller to add to Team"
                placeholder="Search for seller"
                limit={5}
                data={availableSellers?.map((member: Seller) => ({
                    value: member.person.id,
                    label: member.person.name
                })) || []}
                onChange={(value) => handleSelectSellerChange(value)}
            />
            <Button onClick={handleAddSelectSellerToTeam} mb="md">Add Seller to Team</Button>

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
                    {teamDetails?.members?.map((member: Seller) => (
                        <Table.Tr>
                            <Table.Td>{member.person.name}</Table.Td>
                            <Table.Td>{member.person.surname}</Table.Td>
                            <Table.Td>{member.person.email}</Table.Td>
                            <Table.Td>{member.totalSales}</Table.Td>
                            <Table.Td><Button color={"red"}>Remove from Team</Button></Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    )
}
