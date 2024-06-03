import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {Client, CreateClientInputModel, UpdateClientInputModel} from "./models/ClientModel";

const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    Authorization: "Bearer " + localStorage.getItem("token"),
}

export function useCreateClient() {
    return useMutation({
        mutationFn: async (input: CreateClientInputModel) =>
            fetch(ApiUris.createClient, {
                method: "POST",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            })
    });
}

export function useGetClients(lastKeySeen: string = "0") {
    return useQuery<Client[]>({
        queryKey: ["clients", lastKeySeen],
        queryFn: () =>
            fetch(ApiUris.getClients(lastKeySeen), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    });
}

export function useGetClient(id: string) {
    return useQuery<Client>({
        queryKey: [`client-${id}`],
        queryFn: () =>
            fetch(ApiUris.getClient(id), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    })
}

export function useUpdateClient(id: string) {
    return useMutation({
        mutationFn: (newClientInfo: UpdateClientInputModel) =>
            fetch(ApiUris.updateClient(id), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(newClientInfo),
            }),
        /* onMutate: (newClientInfo: UpdateClientInputModel) => {
       /*    queryClient.setQueryData(["clients"], (prevClients: any) =>
            prevClients?.map((prevClient: Client) =>
              prevClient.id === newClientInfo.id ? newClientInfo : prevClient
            )
          );
        }, */
    });
}

//DELETE hook (delete client in api)
export function useDeleteClient() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (clientId: string) =>
            fetch(ApiUris.deleteClient(clientId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['clients']});
        },
    });
}