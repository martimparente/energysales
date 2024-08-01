import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query'
import {ApiUris} from './ApiUris'
import {
    AddTeamAvatarInputModel,
    AddTeamSellerInputModel,
    AddTeamServiceInputModel,
    CreateTeamInputModel,
    DeleteTeamSellerInput,
    DeleteTeamServiceInput,
    Team,
    TeamDetails,
    UpdateTeamInputModel
} from './models/TeamModel'
import {ManagerInfo, Seller} from './models/UserModel.tsx'
import {fetchData, mutateData, mutateData1} from './ApiUtils.tsx'

export function useCreateTeam() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: async (input: CreateTeamInputModel) => mutateData(ApiUris.createTeam, 'POST', input),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teams']})
        }
    })
}

export function useGetTeams(lastKeySeen: string = '0') {
    return useQuery<Team[]>({
        queryKey: ['teams', lastKeySeen],
        queryFn: () => fetchData<Team[]>(ApiUris.getTeams(lastKeySeen))
    })
}

export function useGetTeam(id: string) {
    return useQuery<Team>({
        queryKey: [`team-${id}`],
        queryFn: () => fetchData<Team>(ApiUris.getTeam(id))
    })
}

export function useGetTeamDetails(id: string) {
    return useQuery<TeamDetails>({
        queryKey: [`teamDetails`],
        queryFn: () => fetchData<TeamDetails>(ApiUris.getTeamDetails(id))
    })
}

interface UpdateTeamMutationInput {
    id: string
    input: UpdateTeamInputModel
}

export function useUpdateTeam() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: ({id, input}: UpdateTeamMutationInput) => mutateData(ApiUris.updateTeam(id), 'PUT', input),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
        }
    })
}

export function useDeleteTeam() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: (teamId: string) => mutateData(ApiUris.deleteTeam(teamId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teams']})
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

// add member to team
export function useAddTeamSeller() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (input: AddTeamSellerInputModel) => mutateData(ApiUris.addTeamSeller(input.teamId), 'PUT', input),
        onSuccess: () => {
            // Invalidate and re-fetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// delete member of team
export function useDeleteTeamSeller() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (input: DeleteTeamSellerInput) => mutateData(ApiUris.deleteTeamSeller(input.teamId, input.sellerId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// add service to team
export function useAddServiceToTeam() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (input: AddTeamServiceInputModel) => mutateData(ApiUris.addServiceToTeam(input.teamId), 'PUT', input),
        onSuccess: () => {
            // Invalidate and re-fetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
            queryClient.invalidateQueries({queryKey: ['availableSellers']})
        }
    })
}

// delete service of team
export function useDeleteServiceFromTeam() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (input: DeleteTeamServiceInput) =>
            mutateData(ApiUris.deleteServiceFromTeam(input.teamId, input.serviceId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
        }
    })
}

export function useUploadTeamAvatar() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: async (input: AddTeamAvatarInputModel) => {
            const formData = new FormData()
            formData.append('teamId', input.teamId)
            formData.append('file', input.avatarImg)

            return mutateData1(ApiUris.uploadAvatar(input.teamId), 'POST', formData)
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
        }
    })
}
