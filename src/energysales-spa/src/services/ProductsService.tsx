import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {CreateProductInputModel, Product, UpdateProductInputModel} from "./models/ProductModel";

const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    Authorization: "Bearer " + localStorage.getItem("token"),
}

export function useCreateProduct() {
    return useMutation({
        mutationFn: async (input: CreateProductInputModel) =>
            fetch(ApiUris.createProduct, {
                method: "POST",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(input),
            })
    });
}

export function useGetProducts(lastKeySeen: string = "0") {
    return useQuery<Product[]>({
        queryKey: ["products", lastKeySeen],
        queryFn: () =>
            fetch(ApiUris.getProducts(lastKeySeen), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    });
}

export function useGetProduct(id: string) {
    return useQuery<Product>({
        queryKey: [`product-${id}`],
        queryFn: () =>
            fetch(ApiUris.getProduct(id), {
                headers: AUTHORIZATION_HEADER,
            }).then((res) => res.json()),
    })
}

export function useUpdateProduct(id: string) {
    return useMutation({
        mutationFn: (newProductInfo: UpdateProductInputModel) =>
            fetch(ApiUris.updateProduct(id), {
                method: "PUT",
                headers: AUTHORIZATION_HEADER,
                body: JSON.stringify(newProductInfo),
            }),
        /* onMutate: (newProductInfo: UpdateProductInputModel) => {
       /*    queryProduct.setQueryData(["Products"], (prevProducts: any) =>
            prevProducts?.map((prevProduct: Product) =>
              prevProduct.id === newProductInfo.id ? newProductInfo : prevProduct
            )
          );
        }, */
    });
}

//DELETE hook (delete product in api)
export function useDeleteProduct() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (productId: string) =>
            fetch(ApiUris.deleteProduct(productId), {
                method: "DELETE",
                headers: AUTHORIZATION_HEADER,
            }),
        onSuccess: () => {
            // Invalidate and refetch the teams query to get the updated list
            queryClient.invalidateQueries({queryKey: ['products']});
        },
    });
}