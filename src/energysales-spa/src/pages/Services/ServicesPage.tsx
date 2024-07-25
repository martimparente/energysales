import {AgGridReact} from 'ag-grid-react'; // React Data Grid Component
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css"; // Optional Theme applied to the Data Grid
import {useServicesPage} from "./useServicesPage";
import {Button, Group} from "@mantine/core";
import {IconPlus} from "@tabler/icons-react";
import {useMemo} from "react";
import {SizeColumnsToFitGridStrategy} from "ag-grid-community";

export function ServicesPage() {
    const {
        services,
        columnDefs,
        defaultColDef,
        gridRef,
        onCreateServiceButtonClick,
        onCellEditRequest,
        colorScheme
    } = useServicesPage();

    const autoSizeStrategy = useMemo<SizeColumnsToFitGridStrategy>(
        () => ({
            type: "fitGridWidth",
        }),
        []
    );

    return (
        <div>
            <Group mb="lg" justify="space-between">
                <h1>Services</h1>
                <Button onClick={onCreateServiceButtonClick} color="blue" ><IconPlus size={16}/></Button>
            </Group>

            <div className={colorScheme === 'dark' ? "ag-theme-quartz-dark" : "ag-theme-quartz"} style={{height: 500}}>
                <AgGridReact
                    rowData={services}
                    columnDefs={columnDefs}
                    ref={gridRef}
                    defaultColDef={defaultColDef}
                    rowSelection="multiple"
                    suppressRowClickSelection={true}
                    readOnlyEdit={true}
                    autoSizeStrategy={autoSizeStrategy}
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