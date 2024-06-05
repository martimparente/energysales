import {useCreateClient, useDeleteClient, useGetClients, useUpdateClient} from '../../services/ClientsService';
import {useNavigate} from "react-router-dom"
import {Client, CreateClientInputModel, UpdateClientInputModel} from '../../services/models/ClientModel';
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useClientsPage() {

    const navigate = useNavigate();

    const {data, error: fetchError, isFetching} = useGetClients();
    const {mutateAsync: createClient} = useCreateClient();
    const {mutateAsync: updateClient} = useUpdateClient("");
    const {mutateAsync: deleteClient} = useDeleteClient();

    const [error, setError] = useState<string | null>(null)

    const columns: Column[] = [
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

    if (fetchError && !error) {
        setError(fetchError.message);
    }


    return {
        columns,
        data,
        createClient: async (input: CreateClientInputModel) => await createClient(input).catch(e => setError(e)),
        updateClient: async (input: UpdateClientInputModel) => await updateClient(input).catch(e => setError(e)),
        deleteClient: async (client: Client) => await deleteClient(client.id).catch(e => setError(e)),
        onShowClickHandler: (client: Client) => navigate(`/clients/${client.id}`),
        isFetching,
        error,
    }
}