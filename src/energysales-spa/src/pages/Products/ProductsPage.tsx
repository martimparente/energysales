import {useProductsPage} from './useProductsPage.tsx';
import {Product} from '../../services/models/ProductModel';
import {Button, Table} from "@mantine/core";
import {IconTrash} from "@tabler/icons-react";

export function ProductsPage() {
    const {
        columns,
        data,
        deleteProduct,
        onShowClickHandler,
    } = useProductsPage();

    return (
        <div>
            <h1>Products</h1>
            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {data?.map((product: Product) => (
                        <Table.Tr key={product.id}>
                            <Table.Td>{product.name}</Table.Td>
                            <Table.Td>{product.price}</Table.Td>
                            <Table.Td>{product.description}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowClickHandler(product)}>Show</Button>
                                <Button color={"green"}>Edit</Button>
                                <Button onClick={() => deleteProduct(product)} color={"red"}><IconTrash
                                    stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}