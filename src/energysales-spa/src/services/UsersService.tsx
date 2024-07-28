import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {CreateUserInputModel, PatchUserInputModel, User} from "./models/UserModel.tsx";
import {ApiUris} from "./ApiUris";
import {fetchData, mutateData} from "./ApiUtils.tsx";


export function useCreateUser() {
    return useMutation({
        mutationFn: (input: CreateUserInputModel) => mutateData(ApiUris.createUser, "POST", input),
    });
}

export function useGetUsers(lastKeySeen: string = "0") {
    return useQuery<User[]>({
        queryKey: ["users", lastKeySeen],
        queryFn: () => fetchData<User[]>(ApiUris.getUsers(lastKeySeen)),
    });
}

export function useGetUser(id: string) {
    return useQuery<User>({
        queryKey: [`user-${id}`],
        queryFn: () => fetchData<User>(ApiUris.getUser(id)),
    });
}

export function useUpdateUser() {
    return useMutation({
        mutationFn: (newUserInfo: PatchUserInputModel) =>
            mutateData(ApiUris.updateUser(newUserInfo.id), "PATCH", newUserInfo),
    });
}

export function useDeleteUser() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (userId: string) => mutateData(ApiUris.deleteUser(userId), "DELETE"),
        onSuccess: () => {
            // Invalidate and refetch the users query to get the updated list
            queryClient.invalidateQueries({queryKey: ['users']});
        },
    });
}
