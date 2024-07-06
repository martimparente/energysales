import {useServicesPage} from './useServicesPage.tsx';
import {Service} from '../../services/models/ServiceModel';
import {Button, Table} from "@mantine/core";
import {IconTrash} from "@tabler/icons-react";

export function ServicesPage() {
    const {
        columns,
        services,
        onDeleteServiceClick,
        onShowServiceButtonClick,
        onCreateServiceButtonClick,
    } = useServicesPage();

    return (
        <div>
            <h1>Services</h1>
            <Button onClick={() => onCreateServiceButtonClick()} color={"green"}>Create Service</Button>
            <Table>
                <Table.Thead>
                    <Table.Tr>
                        {columns.map(column => (
                            <Table.Th key={column.header}>{column.header}</Table.Th>
                        ))}
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {services?.map((service: Service) => (
                        <Table.Tr key={service.id}>
                            <Table.Td>{service.name}</Table.Td>
                            <Table.Td>{service.cycleName}</Table.Td>
                            <Table.Td>{service.cycleType}</Table.Td>
                            <Table.Td>{service.description}</Table.Td>
                            <Table.Td>
                                <Button onClick={() => onShowServiceButtonClick(service)} color={"orange"}>Show</Button>
                                <Button onClick={() => onDeleteServiceClick(service)} color={"red"}><IconTrash
                                    stroke={2}/></Button>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </div>
    );
}