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
import {Seller} from "./models/SellersModel.tsx";
import {fetchData, mutateData} from "./ApiUtils.tsx";


export function useCreateTeam() {
    return useMutation({
        mutationFn: (input: CreateTeamInputModel) =>
            mutateData(ApiUris.createTeam, "POST", input)
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

//TODO FIX BUG NOT INVALIDATING QUERY
export function useDeleteTeam() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (teamId: string) =>
            mutateData(ApiUris.deleteTeam(teamId), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['teams']});
        },
    });
}

export function useGetAvailableSellers(lastKeySeen: string = "0") {
    return useQuery<Seller[]>({
        queryKey: ["availableSellers", lastKeySeen],
        queryFn: () => fetchData<Seller[]>(ApiUris.getSellersWithNoTeam(lastKeySeen)),
    });
}

export function useAddTeamSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (input: AddTeamSellerInputModel) =>
            mutateData(ApiUris.addTeamSeller(input.teamId), "PUT", input),
        onSuccess: () => {
            console.log("onSuccess")
            queryClient.invalidateQueries({queryKey: ["teamDetails"]});
            queryClient.invalidateQueries({queryKey: ["availableSellers"]});
        },
    });
}

export function useDeleteTeamSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (input: DeleteTeamSellerInput) =>
            mutateData(ApiUris.deleteTeamSeller(input.teamId, input.sellerId), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ["teamDetails"]});
            queryClient.invalidateQueries({queryKey: ["availableSellers"]});
        },
    });
}
