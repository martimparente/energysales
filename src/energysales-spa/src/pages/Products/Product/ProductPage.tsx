import {useParams} from 'react-router-dom';
import {useGetProduct} from '../../../services/ProductsService';

export function ProductPage() {
    const {id} = useParams<string>();
    console.log(id);
    const {data, isPending, isError} = useGetProduct(id || '');

    if (isError) return <p>error</p>
    if (isPending) return <p>loading</p>

    return (
        <div>
            <h1>Product</h1>
            <p>Name = {data.name}</p>
            <p>Price = {data.price}</p>
            <p>Description = {data.description}</p>
        </div>
    )
}
