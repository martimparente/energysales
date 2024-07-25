import {useCreateService, useDeleteService, useGetServices, useUpdateService} from '../../services/ServicesService';
import {useNavigate} from "react-router-dom";
import {PatchServiceInputModel, Service} from '../../services/models/ServiceModel';
import {useMemo, useRef, useState} from "react";
import {CellEditRequestEvent, ColDef} from "ag-grid-community";
import {AgGridReact} from "ag-grid-react";
import {toast} from "react-toastify";
import {useMantineColorScheme} from "@mantine/core";
import {UserActionsCellRenderer} from "../../components/tableCells/UserActionsCell.tsx";

export function useServicesPage() {
    const navigate = useNavigate();
    const {colorScheme} = useMantineColorScheme({keepTransitions: true});
    const gridRef = useRef<AgGridReact>(null);
    const {data: services, error: fetchError, isFetching} = useGetServices();
    const {mutateAsync: createService} = useCreateService();
    const {mutateAsync: updateService} = useUpdateService();
    const {mutateAsync: deleteService} = useDeleteService();
    const [error, setError] = useState<string>(null);

    const [columnDefs] = useState<ColDef[]>([
        {field: 'name'},
        {field: 'cycleName'},
        {field: 'cycleType'},
        {field: 'description'},
        {
            field: "actions",
            cellRenderer: UserActionsCellRenderer,
            cellRendererParams: {
                onDeleteButtonClick: async (service: Service) => await deleteService(service.id).catch(e => setError(e))
            },
            minWidth: 100
        }
    ]);

    const onShowServiceButtonClick = (service: Service) => {
        navigate(`/services/${service.id}`);
    }

    const defaultColDef = useMemo(() => {
        return {
            filter: 'agTextColumnFilter',
            editable: true,
            floatingFilter: true,
            enableCellChangeFlash: true,
        };
    }, []);

    const onCellEditRequest = async (event: CellEditRequestEvent<Service[]> ) => {
        // optimistic update
        event.node.data[event.colDef.field] = event.newValue;
        event.api.refreshCells({rowNodes: [event.node], columns: [event.column.colId]});

        // update the service in the backend
        try {
            const patchServiceInput = {
                id: event.data.id,
                [event.colDef.field]: event.newValue
            }
            await updateService(patchServiceInput as unknown as PatchServiceInputModel);
            toast.success("Service updated successfully");

        } catch (e) {
            // rollback the change
            event.node.data[event.colDef.field] = event.oldValue;
            event.api.refreshCells({rowNodes: [event.node], columns: [event.column.colId]});
            toast.warning("Service not updated. Try again later");
        }
    };

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    return {
        services,
        columnDefs,
        defaultColDef,
        gridRef,
        onCellEditRequest,
        onCreateServiceButtonClick: () => navigate('/services/create'),
        onShowServiceButtonClick: (service: Service) => navigate(`/services/${service.id}`),
        colorScheme,
        isFetching,
        error,
    }
}