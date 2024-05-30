import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {CreateSellerInputModel, Seller, UpdateSellerInputModel} from "./models/SellersModel.tsx";
import {ApiUris} from "./ApiUris"; // Adjust the import path as necessary

const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    Authorization: "Bearer " + localStorage.getItem("token"),
}

//CREATE hook (post new seller to api)
export function useCreateSeller() {
    return useMutation({
        mutationFn: async (input: CreateSellerInputModel) => {
            return fetch(ApiUris.createSeller, {
                method: "POST",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            });
        },
    });
}

//READ hook (get sellers from api)
export function useGetSellers(lastKeySeen: string = "0") {
    return useQuery<Seller[]>({
        queryKey: ["sellers", lastKeySeen],
        queryFn: () =>
            fetch(ApiUris.getSellers(lastKeySeen), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    });
}

export function useGetSeller(id: string) {
    return useQuery<Seller>({
        queryKey: [`seller-${id}`],
        queryFn: () =>
            fetch(ApiUris.getSeller(id), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    })
}


//UPDATE hook (put seller in api)
export function useUpdateSeller(id: string) {
    return useMutation({
        mutationFn: (newSellerInfo: UpdateSellerInputModel) =>
            fetch(ApiUris.updateSeller(id), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(newSellerInfo),
            }),
    });
}

//DELETE hook (delete seller in api)
export function useDeleteSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (sellerId: string) =>
            fetch(ApiUris.deleteSeller(sellerId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['sellers']});
        },
    });
}