import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {CreateSellerInputModel, Seller, UpdateSellerInputModel} from "./models/SellersModel";
import {ApiUris} from "./ApiUris";
import {AUTHORIZATION_HEADER, fetchData, mutateData} from "./ApiUtils.tsx";


export function useCreateSeller() {
    return useMutation({
        mutationFn: (input: CreateSellerInputModel) =>
            mutateData(ApiUris.createSeller, "POST", input),
    });
}

export function useGetSellers(lastKeySeen: string = "0") {
    return useQuery<Seller[]>({
        queryKey: ["sellers", lastKeySeen],
        queryFn: () => fetchData<Seller[]>(ApiUris.getSellers(lastKeySeen)),
    });
}

export function useGetSeller(id: string) {
    return useQuery<Seller>({
        queryKey: [`seller-${id}`],
        queryFn: () => fetchData<Seller>(ApiUris.getSeller(id)),
    });
}

export function useUpdateSeller(id: string) {
    return useMutation({
        mutationFn: (newSellerInfo: UpdateSellerInputModel) =>
            mutateData(ApiUris.updateSeller(id), "PUT", newSellerInfo),
    });
}

export function useDeleteSeller() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (sellerId: string) =>
            fetch(ApiUris.deleteSeller(sellerId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the sellers query to get the updated list
            queryClient.invalidateQueries({queryKey: ['sellers']});
        },
    });
}
