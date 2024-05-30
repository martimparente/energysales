import {useSellersPage} from './useSellersPage.tsx';
import {Button, Table} from "@mantine/core";
import {Seller} from "../../services/models/SellersModel.tsx";
import {IconTrash} from "@tabler/icons-react";

export function SellersPage() {
    const {
        columns,
        data: sellers,
        createSeller,
        updateSeller,
        deleteSeller,
        onShowClickHandler,
        isFetching,
        error
    } = useSellersPage();

    return (
        <div>
            <h1>Sellers</h1>
            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {sellers?.map((seller: Seller) => (
                        <Table.Tr>
                            <Table.Td>{seller.person.name}</Table.Td>
                            <Table.Td>{seller.person.surname}</Table.Td>
                            <Table.Td>{seller.person.email}</Table.Td>
                            <Table.Td>{seller.totalSales}</Table.Td>
                            <Table.Td>{seller.person.team}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowClickHandler(seller)}>Show</Button>
                                <Button color={"green"}>Edit</Button>
                                <Button onClick={() => deleteSeller(seller)} color={"red"}><IconTrash
                                    stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    )

}