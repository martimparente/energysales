import {AgGridReact} from 'ag-grid-react'; // React Data Grid Component
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css"; // Optional Theme applied to the Data Grid
import {useUsersPage} from "./useUsersPage.tsx";
import {Button, Group, useMantineColorScheme} from "@mantine/core";
import {IconPlus} from "@tabler/icons-react";

export function UsersPage() {

    const {colorScheme, toggleColorScheme} = useMantineColorScheme({
        keepTransitions: true,
    });

    const {
        users,
        columnDefs,
        defaultColDef,
        gridRef,
        onAddUserButtonClick,
        onCellEditRequest,
    } = useUsersPage();

    return (
        <div>
            <Group position="apart" mb="lg" justify="space-between">
                <h1>Users</h1>
                <Button onClick={onAddUserButtonClick} color="blue" leftIcon={<IconPlus size={16}/>}>+</Button>

            </Group>

            <div className={colorScheme === 'dark' ? "ag-theme-quartz-dark" : "ag-theme-quartz"} style={{height: 500}}>
                <AgGridReact
                    rowData={users}
                    columnDefs={columnDefs}
                    ref={gridRef}
                    defaultColDef={defaultColDef}
                    rowSelection="multiple"
                    suppressRowClickSelection={true}
                    readOnlyEdit={true}
                    undoRedoCellEditing={true}
                    pagination={true}
                    paginationPageSize={10}
                    paginationPageSizeSelector={[10, 25, 50]}
                    onCellEditRequest={onCellEditRequest}
                />
            </div>
        </div>
    )
}
