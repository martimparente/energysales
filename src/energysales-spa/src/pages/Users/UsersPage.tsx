import {AgGridReact} from 'ag-grid-react' // React Data Grid Component
import 'ag-grid-community/styles/ag-grid.css' // Mandatory CSS required by the Data Grid
import 'ag-grid-community/styles/ag-theme-quartz.css' // Optional Theme applied to the Data Grid
import {useUsersPage} from './useUsersPage.tsx'
import {Box, Button, Group, Title} from '@mantine/core'
import {IconPlus} from '@tabler/icons-react'

export function UsersPage() {
    const {
        users,
        columnDefs,
        defaultColDef,
        gridRef,
        onAddUserButtonClick,
        onCellEditRequest,
        colorScheme,
        autoSizeStrategy
    } =
        useUsersPage()

    return (
        <Box p='md'>
            <Group mb='lg' justify='space-between'>
                <Title>Users</Title>
                <Button onClick={onAddUserButtonClick} color='blue'>
                    <IconPlus size={16}/>
                </Button>
            </Group>

            <div className={colorScheme === 'dark' ? 'ag-theme-quartz-dark' : 'ag-theme-quartz'} style={{height: 500}}>
                <AgGridReact
                    rowData={users}
                    columnDefs={columnDefs}
                    ref={gridRef}
                    defaultColDef={defaultColDef}
                    rowSelection='multiple'
                    suppressRowClickSelection={true}
                    readOnlyEdit={true}
                    undoRedoCellEditing={true}
                    autoSizeStrategy={autoSizeStrategy}
                    onCellEditRequest={onCellEditRequest}
                />
            </div>
        </Box>
    )
}
