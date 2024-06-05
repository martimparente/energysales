import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {CreateProductInputModel, Product, UpdateProductInputModel} from "./models/ProductModel";
import {fetchData, mutateData} from "./ApiUtils.tsx";

export function useCreateProduct() {
    return useMutation({
        mutationFn: (input: CreateProductInputModel) =>
            mutateData(ApiUris.createProduct, "POST", input),
    });
}

export function useGetProducts(lastKeySeen: string = "0") {
    return useQuery<Product[]>({
        queryKey: ["products", lastKeySeen],
        queryFn: () => fetchData<Product[]>(ApiUris.getProducts(lastKeySeen)),
    });
}

export function useGetProduct(id: string) {
    return useQuery<Product>({
        queryKey: [`product-${id}`],
        queryFn: () => fetchData<Product>(ApiUris.getProduct(id)),
    });
}

export function useUpdateProduct(id: string) {
    return useMutation({
        mutationFn: (newProductInfo: UpdateProductInputModel) =>
            mutateData(ApiUris.updateProduct(id), "PUT", newProductInfo),
    });
}

export function useDeleteProduct() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (productId: string) =>
            mutateData(ApiUris.deleteProduct(productId), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['products']});
        },
    });
}
