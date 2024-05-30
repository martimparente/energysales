import {useCreateClient, useDeleteClient, useGetClients, useUpdateClient} from '../../services/ClientsService';
import {useNavigate} from "react-router-dom"
import {Client, CreateClientInputModel, UpdateClientInputModel} from '../../services/models/ClientModel';
import {Column} from '../../components/GenericTable';

export function useClientsPage() {

    const navigate = useNavigate();

    const {data, error, isFetching} = useGetClients();
    const {mutateAsync: createClient} = useCreateClient();
    const {mutateAsync: updateClient} = useUpdateClient();
    const {mutateAsync: deleteClient} = useDeleteClient();

    const columns: Column<Client>[] = [
        {
            accessor: 'name',
            header: 'Name',
            sortable: true,
        },
        {
            accessor: 'nif',
            header: 'Nif',
            sortable: true,
        },
        {
            accessor: 'phone',
            header: 'Phone',
            sortable: true,
        },
        {
            accessor: 'district',
            header: 'District',
            sortable: true,
        },
    ];

    return {
        columns,
        data,
        createClient: async (input: CreateClientInputModel) => await createClient(input),
        updateClient: async (input: UpdateClientInputModel) => await updateClient(input),
        deleteClient: async (client: Client) => await deleteClient(client.id),
        onShowClickHandler: (client: Client) => navigate(`/clients/${client.id}`),
        isFetching,
        error,
    }
}