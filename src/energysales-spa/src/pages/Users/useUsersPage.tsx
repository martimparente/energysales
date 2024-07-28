import {useDeleteUser, useGetUsers, useUpdateUser} from '../../services/UsersService';
import {useNavigate} from "react-router-dom"
import {PatchUserInputModel, User} from "../../services/models/UserModel.tsx";
import {useMemo, useRef, useState} from "react";
import {CellEditRequestEvent, ColDef, SizeColumnsToFitGridStrategy} from "ag-grid-community";
import {AgGridReact} from "ag-grid-react";
import {toast} from "react-toastify";
import {UserActionsCellRenderer} from "../../components/tableCells/UserActionsCell.tsx";
import {useMantineColorScheme} from "@mantine/core";

export function useUsersPage() {
    const navigate = useNavigate();
    const {colorScheme} = useMantineColorScheme({keepTransitions: true});
    const gridRef = useRef<AgGridReact>(null);
    const {data: users, error: fetchError, isFetching} = useGetUsers();
    const {mutateAsync: updateUser} = useUpdateUser();
    const {mutateAsync: deleteUser} = useDeleteUser();
    const [error, setError] = useState<string | null>(null)

    const [columnDefs] = useState<ColDef[]>([
        {field: 'name'},
        {field: 'surname'},
        {field: 'email'},
        {
            field: 'role',
            cellEditorParams: {
                values: ['Seller', 'Manager', 'Admin'],
            },
        },
        {
            field: "actions",
            cellRenderer: UserActionsCellRenderer,
            cellRendererParams: {
                onDeleteButtonClick: async (user: User) => await deleteUser(user.id).catch(e => setError(e))
            },
            filter: false,
            width: 100,
            minWidth: 10
        }
    ]);

    const defaultColDef = useMemo(() => {
        return {
            filter: 'agTextColumnFilter',
            editable: true,
            floatingFilter: true,
            enableCellChangeFlash: true,
        };
    }, []);

    const autoSizeStrategy = useMemo<SizeColumnsToFitGridStrategy>(
        () => ({
            type: "fitGridWidth",
        }),
        []
    );

    const onCellEditRequest = async (event: CellEditRequestEvent<User[]>) => {
        // optimistic update
        event.node.data[event.colDef.field] = event.newValue;
        event.api.refreshCells({rowNodes: [event.node], columns: [event.column.colId]});

        // update the user in the backend
        try {
            const patchUserInput = {
                id: event.data.id,
                [event.colDef.field]: event.newValue
            }
            await updateUser(patchUserInput as unknown as PatchUserInputModel);
            toast.success("User updated successfully");

        } catch (e) {
            // rollback the change
            event.node.data[event.colDef.field] = event.oldValue;
            event.api.refreshCells({rowNodes: [event.node], columns: [event.column.colId]});
            toast.warning("User not updated. Try again later");
        }
    };

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    return {
        users,
        columnDefs,
        defaultColDef,
        gridRef,
        autoSizeStrategy,
        onCellEditRequest,
        onAddUserButtonClick: () => navigate('/users/create'),
        onShowUserButtonClick: (User: User) => navigate(`/users/${User.id}`),
        colorScheme,
        isFetching,
        error,
    }
}