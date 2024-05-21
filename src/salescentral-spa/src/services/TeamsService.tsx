import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Team } from "../interfaces/Teams";
import { ApiUris } from "./ApiUris"; // Adjust the import path as necessary

const AUTHORIZATION_HEADER = {
  "Content-Type": "application/json",
  Authorization: "Bearer " + localStorage.getItem("token"),
}

//CREATE hook (post new team to api)
export function useCreateTeam() {
  return useMutation({
    mutationFn: async (team: Team) => {
      const teamPayload = {
        name: team.name,
        manager: team.manager,
        location: { district: team.district },
      };
      return fetch(ApiUris.createTeam, {
        method: "POST",
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(teamPayload),
      });
    },
  });
}

//READ hook (get teams from api)
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


//UPDATE hook (put team in api)
export function useUpdateTeam() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (newTeamInfo: Team) =>
      fetch(ApiUris.updateTeam(newTeamInfo.id), {
        method: "PUT",
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(newTeamInfo),
      }),
    onMutate: (newTeamInfo: Team) => {
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