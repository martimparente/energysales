import {useClientsPage} from './useClientsPage.tsx';
import {Client} from '../../services/models/ClientModel';
import {Button, Table} from '@mantine/core'
import {IconTrash} from "@tabler/icons-react";

export function ClientsPage() {
    const {
        columns,
        data: clients,
        deleteClient,
        onShowClickHandler,
    } = useClientsPage();

    return (
        <div>
            <h1>Clients</h1>
            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {clients?.map((client: Client) => (
                        <Table.Tr key={client.id}>
                            <Table.Td>{client.name}</Table.Td>
                            <Table.Td>{client.nif}</Table.Td>
                            <Table.Td>{client.phone}</Table.Td>
                            <Table.Td>{client.location.district}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowClickHandler(client)} color={"orange"}>Show</Button>
                                <Button color={"green"}>Edit</Button>
                                <Button onClick={() => deleteClient(client)} color={"red"}><IconTrash
                                    stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}