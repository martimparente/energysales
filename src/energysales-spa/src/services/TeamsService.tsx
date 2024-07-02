import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {
    AddTeamSellerInputModel,
    CreateTeamInputModel,
    DeleteTeamSellerInput,
    Team,
    TeamDetails,
    UpdateTeamInputModel
} from "./models/TeamModel";
import {ManagerInfo, User} from "./models/UserModel.tsx";
import {AUTHORIZATION_HEADER, fetchData, mutateData} from "./ApiUtils.tsx";


export function useCreateTeam() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (input: CreateTeamInputModel) =>
            fetch(ApiUris.createTeam, {
                method: "POST",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teams']});
        },
    });
}

export function useGetTeams(lastKeySeen: string = "0") {
    return useQuery<Team[]>({
        queryKey: ["teams", lastKeySeen],
        queryFn: () => fetchData<Team[]>(ApiUris.getTeams(lastKeySeen)),
    });
}

export function useGetTeam(id: string) {
    return useQuery<Team>({
        queryKey: [`team-${id}`],
        queryFn: () => fetchData<Team>(ApiUris.getTeam(id)),
    });
}

export function useGetTeamDetails(id: string) {
    return useQuery<TeamDetails>({
        queryKey: [`teamDetails`],
        queryFn: () => fetchData<TeamDetails>(ApiUris.getTeamDetails(id)),
    });
}

export function useUpdateTeam() {
    return useMutation({
        mutationFn: (input: UpdateTeamInputModel) =>
            mutateData(ApiUris.updateTeam(input.id), "PUT", input),
    });
}

export function useDeleteTeam() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (teamId: string) =>
            fetch(ApiUris.deleteTeam(teamId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['teams']});
        },
    });
}

export function useGetAvailableSellers(lastKeySeen: string = "0") {
    return useQuery<User[]>({
        queryKey: ["availableSellers", lastKeySeen],
        queryFn: () => fetchData<User[]>(ApiUris.getSellersWithNoTeam(lastKeySeen)),
    });
}

export function useGetManagerCandidates() {
    return useQuery<ManagerInfo[]>({
        queryKey: ["managerCandidates"],
        queryFn: () => fetchData<ManagerInfo[]>(ApiUris.getManagerCandidates()),
    });
}

// add member to team
export function useAddTeamSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (input: AddTeamSellerInputModel) =>
            fetch(ApiUris.addTeamSeller(input.teamId), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            }),
        onSuccess: () => {
            // Invalidate and re-fetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ["teamDetails"]});
            queryClient.invalidateQueries({queryKey: ["availableSellers"]});
        },
    });
}

// delete member to team
export function useDeleteTeamSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (input: DeleteTeamSellerInput) =>
            fetch(ApiUris.deleteTeamSeller(input.teamId, input.sellerId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ["teamDetails"]});
            queryClient.invalidateQueries({queryKey: ["availableSellers"]});
        },
    });
}
