import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query'
import {ApiUris} from './ApiUris'
import {
    AddPartnerAvatarInputModel,
    AddPartnerSellerInputModel,
    AddPartnerServiceInputModel,
    CreatePartnerInputModel,
    DeletePartnerSellerParams,
    DeletePartnerServiceParams,
    Partner,
    PartnerDetails,
    UpdatePartnerInputModel
} from './models/TeamModel'
import {ManagerInfo, Seller} from './models/UserModel.tsx'
import {fetchData, mutateData, mutateData1} from './ApiUtils.tsx'

export function useCreatePartner() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: async (input: CreatePartnerInputModel) => mutateData(ApiUris.createPartner, 'POST', input),
        onSuccess: () => {
            // Invalidate and refetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partners']})
        }
    })
}

export function useGetPartners(lastKeySeen: string = '0') {
    return useQuery<Partner[]>({
        queryKey: ['partners', lastKeySeen],
        queryFn: () => fetchData<Partner[]>(ApiUris.getPartners(lastKeySeen))
    })
}

export function useGetPartner(id: string) {
    return useQuery<Partner>({
        queryKey: [`partner-${id}`],
        queryFn: () => fetchData<Partner>(ApiUris.getPartner(id))
    })
}

export function useGetPartnerDetails(id: string) {
    return useQuery<PartnerDetails>({
        queryKey: [`partnerDetails`],
        queryFn: () => fetchData<PartnerDetails>(ApiUris.getPartnerDetails(id))
    })
}

export function usePatchPartner() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: ({id, input}: {
            id: string
            input: UpdatePartnerInputModel
        }) => mutateData(ApiUris.updatePartner(id), 'PATCH', input),
        onSuccess: () => {
            // Invalidate and refetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
        }
    })
}

export function useDeletePartner() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: (partnerId: string) => mutateData(ApiUris.deletePartner(partnerId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partners']})
        }
    })
}

export function useGetAvailableSellers(searchQuery: string) {
    return useQuery<Seller[]>({
        queryKey: ['availableSellers', searchQuery],
        queryFn: () => fetchData<Seller[]>(ApiUris.getAvailableSellers(searchQuery))
    })
}

export function useGetManagerCandidates() {
    return useQuery<ManagerInfo[]>({
        queryKey: ['managerCandidates'],
        queryFn: () => fetchData<ManagerInfo[]>(ApiUris.getManagerCandidates())
    })
}

// add member to partner
export function useAddPartnerSeller() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async ({partnerId, input}: { partnerId: string; input: AddPartnerSellerInputModel }) =>
            mutateData(ApiUris.addPartnerSeller(partnerId), 'PUT', input),
        onSuccess: () => {
            // Invalidate and re-fetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// delete member of partner
export function useDeletePartnerSeller() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (params: DeletePartnerSellerParams) =>
            mutateData(ApiUris.deletePartnerSeller(params.partnerId, params.sellerId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// add service to partner
export function useAddPartnerService() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async ({partnerId, input}: { partnerId: string; input: AddPartnerServiceInputModel }) =>
            mutateData(ApiUris.addPartnerService(partnerId), 'PUT', input),
        onSuccess: () => {
            // Invalidate and re-fetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// delete service of partner
export function useDeletePartnerService() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (params: DeletePartnerServiceParams) =>
            mutateData(ApiUris.deletePartnerService(params.partnerId, params.serviceId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the partners query to get the updated list
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
        }
    })
}

export function useUploadPartnerAvatar() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: async ({partnerId, input}: { partnerId: string; input: AddPartnerAvatarInputModel }) => {
            const formData = new FormData()
            formData.append('partnerId', partnerId)
            formData.append('file', input.avatarImg)

            return mutateData1(ApiUris.uploadAvatar(partnerId), 'POST', formData)
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['partnerDetails']})
        }
    })
}
