import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {
    AddTeamSellerInputModel,
    CreateTeamInputModel,
    Team,
    TeamDetails,
    UpdateTeamInputModel
} from "./models/TeamModel";
import {notifications} from "@mantine/notifications";
import {Seller} from "./models/SellersModel.tsx";

const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    Authorization: "Bearer " + localStorage.getItem("token"),
}

export function useCreateTeam() {
    return useMutation({
        mutationFn: async (input: CreateTeamInputModel) =>
            fetch(ApiUris.createTeam, {
                method: "POST",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            })
    });
}

export function useGetTeams(lastKeySeen: string = "0") {
    return useQuery<Team[]>({
        queryKey: ["teams", lastKeySeen],
        queryFn: () =>
            fetch(ApiUris.getTeams(lastKeySeen), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    });
}


export function useGetTeam(id: string) {
    return useQuery<Team>({
        queryKey: [`team-${id}`],
        queryFn: () =>
            fetch(ApiUris.getTeam(id), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    })
}


export function useGetTeamDetails(id: string) {
    return useQuery<TeamDetails>({
        queryKey: [`teamDetails-${id}`],
        queryFn: () =>
            fetch(ApiUris.getTeamDetails(id), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    })
}

export function useUpdateTeam() {
    return useMutation({
        mutationFn: (input: UpdateTeamInputModel) => //todo id here?
            fetch(ApiUris.updateTeam(input.id), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            }),
        /* onMutate: (newTeamInfo: UpdateTeamInputModel) => {
       /*    queryClient.setQueryData(["teams"], (prevTeams: any) =>
            prevTeams?.map((prevTeam: Team) =>
              prevTeam.id === newTeamInfo.id ? newTeamInfo : prevTeam
            )
          );
        }, */
    });
}

//DELETE hook (delete team in api)
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
            notifications.show({
                title: 'Default notification',
                message: 'Hey there, your code is awesome! 🤥',
            })
        },
    });
}

// add member to team
export function useAddTeamSeller() {
    return useMutation({
        mutationFn: async (input: AddTeamSellerInputModel) =>
            fetch(ApiUris.addTeamSeller(input.teamId), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            })
    });
}

// get available sellers
export function useGetAvailableSellers(lastKeySeen: string = "0") {
    return useQuery<Seller[]>({
        queryKey: ["availableSellers", lastKeySeen],
        queryFn: () =>
            fetch(ApiUris.getSellersWithNoTeam(lastKeySeen), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    });
}

