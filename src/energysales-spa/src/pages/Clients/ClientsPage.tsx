import {AgGridReact} from 'ag-grid-react'; // React Data Grid Component
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css"; // Optional Theme applied to the Data Grid
import {useClientsPage} from './useClientsPage.tsx';
import {Button, Group} from '@mantine/core';
import {IconPlus} from "@tabler/icons-react";
import {useMemo} from "react";
import {SizeColumnsToFitGridStrategy} from "ag-grid-community";

export function ClientsPage() {
    const {
        clients,
        columnDefs,
        defaultColDef,
        gridRef,
        onCreateClientButtonClick,
        colorScheme
    } = useClientsPage();

    const autoSizeStrategy = useMemo<SizeColumnsToFitGridStrategy>(
        () => ({
            type: "fitGridWidth",
        }),
        []
    );

    return (
        <div>
            <Group mb="lg" justify="space-between">
                <h1>Clients</h1>
                <Button onClick={onCreateClientButtonClick} color="blue"><IconPlus size={16}/></Button>
            </Group>

            <div className={colorScheme === 'dark' ? "ag-theme-quartz-dark" : "ag-theme-quartz"} style={{height: 500}}>
                <AgGridReact
                    rowData={clients}
                    columnDefs={columnDefs}
                    ref={gridRef}
                    defaultColDef={defaultColDef}
                    rowSelection="multiple"
                    suppressRowClickSelection={true}
                    autoSizeStrategy={autoSizeStrategy}
                    readOnlyEdit={true}
                    undoRedoCellEditing={true}
                    pagination={true}
                    paginationPageSize={10}
                    paginationPageSizeSelector={[10, 25, 50]}
                />
            </div>
        </div>
    );
}