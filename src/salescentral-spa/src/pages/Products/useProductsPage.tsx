import {useCreateProduct, useDeleteProduct, useGetProducts, useUpdateProduct} from '../../services/ProductsService';
import {useNavigate} from "react-router-dom"
import {CreateProductInputModel, Product, UpdateProductInputModel} from '../../services/models/ProductModel';
import {Column} from '../../components/GenericTable';

export function useProductsPage() {

    const navigate = useNavigate();

    const {data, error, isFetching} = useGetProducts();
    const {mutateAsync: createProduct} = useCreateProduct();
    const {mutateAsync: updateProduct} = useUpdateProduct();
    const {mutateAsync: deleteProduct} = useDeleteProduct();

    const columns: Column<Product>[] = [
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

    return {
        columns,
        data,
        createProduct: async (input: CreateProductInputModel) => await createProduct(input),
        updateProduct: async (input: UpdateProductInputModel) => await updateProduct(input),
        deleteProduct: async (product: Product) => await deleteProduct(product.id),
        onShowClickHandler: (product: Product) => navigate(`/products/${product.id}`),
        isFetching,
        error,
    }
}