import {useParams} from 'react-router-dom';
import {useGetSeller} from '../../../services/SellersService';

export function SellerPage() {
    const {id} = useParams<string>();
    console.log(id);
    const {data, isPending, isError} = useGetSeller(id || '');

    if (isError) return <p>error</p>
    if (isPending) return <p>loading</p>

    return (
        <div>
            <h1>Seller</h1>
            <p>Name = {data.person.name}</p>
            <p>Surname = {data.person.surname}</p>
            <p>Email = {data.person.email}</p>
            <p>Team = {data.team}</p>
            <p>TotalSales = {data.totalSales}</p>
        </div>
    )
}
