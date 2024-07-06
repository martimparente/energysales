import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {CreateUserInputModel, User, UpdateUserInputModel} from "./models/UserModel.tsx";
import {ApiUris} from "./ApiUris";
import {AUTHORIZATION_HEADER, fetchData, mutateData} from "./ApiUtils.tsx";


export function useCreateUser() {
    return useMutation({
        mutationFn: (input: CreateUserInputModel) =>
            mutateData(ApiUris.createUser, "POST", input),
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
        mutationFn: (newUserInfo: UpdateUserInputModel) =>
            mutateData(ApiUris.updateUser(newUserInfo.id), "PUT", newUserInfo),
    });
}

export function useDeleteUser() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (userId: string) =>
            fetch(ApiUris.deleteUser(userId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the users query to get the updated list
            queryClient.invalidateQueries({queryKey: ['users']});
        },
    });
}
