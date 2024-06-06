import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {Client, CreateClientInputModel, UpdateClientInputModel} from "./models/ClientModel";
import {fetchData, mutateData} from "./ApiUtils.tsx";

export function useCreateClient() {
    return useMutation({
        mutationFn: (input: CreateClientInputModel) =>
            mutateData(ApiUris.createClient, "POST", input),
    });
}

export function useGetClients(lastKeySeen: string = "0") {
    return useQuery<Client[]>({
        queryKey: ["clients", lastKeySeen],
        queryFn: () => fetchData<Client[]>(ApiUris.getClients(lastKeySeen)),
    });
}

export function useGetClient(id: string) {
    return useQuery<Client>({
        queryKey: [`client-${id}`],
        queryFn: () => fetchData<Client>(ApiUris.getClient(id)),
    });
}

export function useUpdateClient(id: string) {
    return useMutation({
        mutationFn: (newClientInfo: UpdateClientInputModel) =>
            mutateData(ApiUris.updateClient(id), "PUT", newClientInfo),
    });
}

export function useDeleteClient() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (clientId: string) =>
            mutateData(ApiUris.deleteClient(clientId), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['clients']});
        },
    });
}
