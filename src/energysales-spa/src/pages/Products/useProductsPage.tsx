import {useCreateProduct, useDeleteProduct, useGetProducts, useUpdateProduct} from '../../services/ProductsService';
import {useNavigate} from "react-router-dom"
import {CreateProductInputModel, Product, UpdateProductInputModel} from '../../services/models/ProductModel';
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useProductsPage() {

    const navigate = useNavigate();

    const {data, error: fetchError,  isFetching} = useGetProducts();
    const {mutateAsync: createProduct} = useCreateProduct();
    const {mutateAsync: updateProduct} = useUpdateProduct("");
    const {mutateAsync: deleteProduct} = useDeleteProduct();

    const [error, setError] = useState<string | null>(null)

    const columns: Column[] = [
        {
            accessor: 'name',
            header: 'Name',
            sortable: true,
        },
        {
            accessor: 'price',
            header: 'Price',
            sortable: true,
        },
        {
            accessor: 'description',
            header: 'Description',
            sortable: true,
        },
    ];

    if (fetchError && !error) {
        setError(fetchError.message);
    }


    return {
        columns,
        data,
        createProduct: async (input: CreateProductInputModel) => await createProduct(input).catch(e => setError(e)),
        updateProduct: async (input: UpdateProductInputModel) => await updateProduct(input).catch(e => setError(e)),
        deleteProduct: async (product: Product) => await deleteProduct(product.id).catch(e => setError(e)),
        onShowClickHandler: (product: Product) => navigate(`/products/${product.id}`),
        isFetching,
        error,
    }
}