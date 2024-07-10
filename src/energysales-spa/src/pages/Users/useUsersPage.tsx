import {useCreateUser, useDeleteUser, useGetUsers, useUpdateUser} from '../../services/UsersService';
import {useNavigate} from "react-router-dom"
import {UpdateUserInputModel, User} from "../../services/models/UserModel.tsx";
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useUsersPage() {

    const navigate = useNavigate();

    const {data: Users, error: fetchError, isFetching} = useGetUsers();
    const {mutateAsync: createUser} = useCreateUser();
    const {mutateAsync: updateUser} = useUpdateUser();
    const {mutateAsync: deleteUser} = useDeleteUser();

    const [error, setError] = useState<string | null>(null)

    const columns: Column[] = [
        {
            accessor: 'name',
            header: 'Name',
            sortable: true,
        },
        {
            accessor: 'surname',
            header: 'Surname',
            sortable: true,
        },
        {
            accessor: 'email',
            header: 'E-Mail',
            sortable: true,
        },
        {
            accessor: 'role',
            header: 'Role',
            sortable: true,
        },
        {
            accessor: 'action',
            header: 'Action',
            sortable: true,
        }
    ];

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    return {
        columns,
        Users,
        onCreateUserButtonClick: () => navigate(`/users/create`),
        updateUser: async (input: UpdateUserInputModel) => await updateUser(input).catch(e => setError(e)),
        onDeleteUserButtonClick: async (User: User) => await deleteUser(User.id).catch(e => setError(e)),
        onShowUserButtonClick: (User: User) => navigate(`/Users/${User.id}`),
        isFetching,
        error,
    }
}