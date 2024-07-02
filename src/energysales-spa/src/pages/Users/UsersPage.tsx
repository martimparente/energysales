import {useUsersPage} from './useUsersPage.tsx';
import {Button, Table} from "@mantine/core";
import {User} from "../../services/models/UserModel.tsx";
import {IconTrash} from "@tabler/icons-react";

export function UsersPage() {
    const {
        columns,
        Users,
        deleteUser,
        onShowClickHandler,

    } = useUsersPage();

    return (
        <div>
            <h1>Users</h1>
            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {Users?.map((User: User) => (
                        <Table.Tr key={User.id}>
                            <Table.Td>{User.name}</Table.Td>
                            <Table.Td>{User.surname}</Table.Td>
                            <Table.Td>{User.email}</Table.Td>
                            <Table.Td>{User.role}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowClickHandler(User)} color={"orange"}>Show</Button>
                                <Button color={"green"}>Edit</Button>
                                <Button onClick={() => deleteUser(User)} color={"red"}><IconTrash
                                    stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    )

}