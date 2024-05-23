import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Team } from "../interfaces/Teams";
import { ApiUris } from "./ApiUris";
import { CreateTeamInputModel, CreateTeamOutputModel, UpdateTeamInputModel } from "./models/TeamModel";

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

export function useUpdateTeam() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (newTeamInfo: UpdateTeamInputModel) =>
      fetch(ApiUris.updateTeam(newTeamInfo.id), {
        method: "PUT",
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(newTeamInfo),
      }),
    onMutate: (newTeamInfo: UpdateTeamInputModel) => {
      queryClient.setQueryData(["teams"], (prevTeams: any) =>
        prevTeams?.map((prevTeam: Team) =>
          prevTeam.id === newTeamInfo.id ? newTeamInfo : prevTeam
        )
      );
    },
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
  });
}