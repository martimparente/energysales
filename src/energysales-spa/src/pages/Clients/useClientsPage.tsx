import {useCreateClient, useDeleteClient, useGetClients, useUpdateClient} from '../../services/ClientService.tsx'
import {useNavigate} from 'react-router-dom'
import {Client} from '../../services/models/ClientModel'
import {useMemo, useRef, useState} from 'react'
import {ColDef} from '@ag-grid-community/core'
import {AgGridReact} from 'ag-grid-react'
import {toast} from 'react-toastify'
import {useMantineColorScheme} from '@mantine/core'
import {ClientActionsCellRenderer} from '../../components/tableCells/UserActionsCell.tsx'
import {PatchServiceInputModel} from '../../services/models/ServiceModel.tsx'
import {CellEditRequestEvent} from 'ag-grid-community'

export function useClientsPage() {
    const navigate = useNavigate()
    const {colorScheme} = useMantineColorScheme({keepTransitions: true})
    const gridRef = useRef<AgGridReact>(null)
    const {data: clients, error: fetchError, isFetching} = useGetClients()
    const {mutateAsync: createClient} = useCreateClient()
    const {mutateAsync: updateClient} = useUpdateClient()
    const {mutateAsync: deleteClient} = useDeleteClient()
    const [error, setError] = useState<string | null>(null)

    const [columnDefs] = useState<ColDef[]>([
        {field: 'name', headerName: 'Name'},
        {field: 'nif', headerName: 'NIF'},
        {field: 'phone', headerName: 'Phone'},
        {
            field: 'location.district',
            headerName: 'District',
            valueGetter: (params) => params.data.location.district
        },
        {
            field: 'actions',
            cellRenderer: ClientActionsCellRenderer,
            cellRendererParams: {
                onMakeOfferButtonClick: (client: Client) => navigate(`/clients/${client.id}/make-offer`),
                onDeleteButtonClick: async (client: Client) => await deleteClient(client.id).catch((e) => setError(e))
            },
            minWidth: 150
        }
    ])

    const defaultColDef = useMemo(() => {
        return {
            filter: 'agTextColumnFilter',
            editable: true,
            floatingFilter: true,
            enableCellChangeFlash: true
        }
    }, [])

    const onDeleteClientClick = async (client: Client) => {
        try {
            await deleteClient(client.id)
            toast.success('Client deleted successfully')
        } catch (e) {
            setError(e.message)
            toast.error('Client not deleted. Try again later')
        }
    }

    const onCellEditRequest = async (event: CellEditRequestEvent<Client[]>) => {
        // optimistic update
        event.node.data[event.colDef.field] = event.newValue
        event.api.refreshCells({
            rowNodes: [event.node],
            columns: [event.column.colId]
        })

        // update the service in the backend
        try {
            const patchClientInput = {
                id: event.data.id,
                [event.colDef.field]: event.newValue
            }
            await updateClient(patchClientInput as unknown as PatchServiceInputModel)
            toast.success('Service updated successfully')
        } catch (e) {
            // rollback the change
            event.node.data[event.colDef.field] = event.oldValue
            event.api.refreshCells({
                rowNodes: [event.node],
                columns: [event.column.colId]
            })
            toast.warning('Service not updated. Try again later')
        }
    }

    if (fetchError && !error) {
        setError(fetchError.message)
    }

    return {
        clients,
        columnDefs,
        defaultColDef,
        gridRef,
        onCreateClientButtonClick: () => navigate(`/clients/create`),
        onShowClickHandler: (client: Client) => navigate(`/clients/${client.id}`),
        colorScheme,
        isFetching,
        error
    }
}
