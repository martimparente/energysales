import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query'
import {ApiUris} from './ApiUris'
import {Client, CreateClientInputModel, MakeOfferInputModel} from './models/ClientModel'
import {fetchData, mutateData} from './ApiUtils.tsx'
import {UpdateServiceInputModel} from './models/ServiceModel.tsx'

export function useCreateClient() {
    return useMutation({
        mutationFn: (input: CreateClientInputModel) => mutateData(ApiUris.createClient, 'POST', input)
    })
}

export function useGetClients(lastKeySeen: string = '0') {
    return useQuery<Client[]>({
        queryKey: ['clients', lastKeySeen],
        queryFn: () => fetchData<Client[]>(ApiUris.getClients(lastKeySeen))
    })
}

export function useGetClient(id: string) {
    return useQuery<Client>({
        queryKey: [`client-${id}`],
        queryFn: () => fetchData<Client>(ApiUris.getClient(id))
    })
}

export function usePatchClient() {
    return useMutation({
        mutationFn: ({clientId, input}: { clientId: string; input: UpdateServiceInputModel }) =>
            mutateData(ApiUris.updateClient(clientId), 'PATCH', input)
    })
}

export function useDeleteClient() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: (clientId: string) => mutateData(ApiUris.deleteClient(clientId), 'DELETE'),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['clients']})
        }
    })
}

export function useMakeOffer() {
    return useMutation({
        mutationFn: (input: MakeOfferInputModel) => mutateData(ApiUris.makeOffer(input.clientId), 'POST', input)
    })
}

export function useSendOfferEmail() {
    return useMutation({
        mutationFn: (clientId: string) => mutateData(ApiUris.sendOfferEmail(clientId), 'POST')
    })
}
