import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {CreateSellerInputModel, Seller, UpdateSellerInputModel} from "./models/SellersModel";
import {ApiUris} from "./ApiUris";
import {fetchData, mutateData} from "./ApiUtils.tsx";


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
            mutateData(ApiUris.deleteSeller(sellerId), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['sellers']});
        },
    });
}
