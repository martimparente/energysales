import {useCreateUser, useDeleteUser, useGetUsers, useUpdateUser} from '../../services/UsersService';
import {useNavigate} from "react-router-dom"
import {PatchUserInputModel, User} from "../../services/models/UserModel.tsx";
import {useMemo, useRef, useState} from "react";
import {CellEditRequestEvent, ColDef} from "@ag-grid-community/core";
import {AgGridReact} from "ag-grid-react";
import {toast} from "react-toastify";
import {UserActionsCellRenderer} from "../../components/tableCells/UserActionsCell.tsx";

export function useUsersPage() {
    const navigate = useNavigate();
    const gridRef = useRef<AgGridReact>(null);
    const {data: users, error: fetchError, isFetching} = useGetUsers();
    const {mutateAsync: createUser} = useCreateUser();
    const {mutateAsync: updateUser} = useUpdateUser();
    const {mutateAsync: deleteUser} = useDeleteUser();
    const [error, setError] = useState<string | null>(null)

    const [columnDefs] = useState<ColDef[]>([
        {field: 'name'},
        {field: 'surname'},
        {field: 'email'},
        {
            field: 'role',
            cellEditor: 'agSelectCellEditor',
            cellEditorParams: {
                values: ['Seller', 'Manager', 'Admin'],
            },
        },
        {
            field: "actions",
            cellRenderer: UserActionsCellRenderer,
            cellRendererParams: {
                onDeleteUserButtonClick: async (user: User) => await deleteUser(user.id).catch(e => setError(e))
            },
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
        onCellEditRequest,
        onAddUserButtonClick: () => navigate('/users/create'),
        onShowUserButtonClick: (User: User) => navigate(`/users/${User.id}`),
        isFetching,
        error,
    }
}