import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query'
import {ApiUris} from './ApiUris'
import {
    AddTeamAvatarInputModel,
    AddTeamSellerInputModel,
    AddTeamServiceInputModel,
    CreateTeamInputModel,
    DeleteTeamSellerParams,
    DeleteTeamServiceParams,
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

export function usePatchTeam() {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: ({id, input}: {
            id: string
            input: UpdateTeamInputModel
        }) => mutateData(ApiUris.updateTeam(id), 'PATCH', input),
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
        mutationFn: async ({teamId, input}: { teamId: string; input: AddTeamSellerInputModel }) =>
            mutateData(ApiUris.addTeamSeller(teamId), 'PUT', input),
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
        mutationFn: async (params: DeleteTeamSellerParams) =>
            mutateData(ApiUris.deleteTeamSeller(params.teamId, params.sellerId), 'DELETE'),
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
        mutationFn: async ({teamId, input}: { teamId: string; input: AddTeamServiceInputModel }) =>
            mutateData(ApiUris.addServiceToTeam(teamId), 'PUT', input),
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
        mutationFn: async (params: DeleteTeamServiceParams) =>
            mutateData(ApiUris.deleteServiceFromTeam(params.teamId, params.serviceId), 'DELETE'),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
        }
    })
}

export function useUploadTeamAvatar() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: async ({teamId, input}: { teamId: string; input: AddTeamAvatarInputModel }) => {
            const formData = new FormData()
            formData.append('teamId', teamId)
            formData.append('file', input.avatarImg)

            return mutateData1(ApiUris.uploadAvatar(teamId), 'POST', formData)
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['teamDetails']})
        }
    })
}
