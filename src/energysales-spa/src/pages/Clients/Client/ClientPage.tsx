import {useParams} from 'react-router-dom'
import {useGetClient} from '../../../services/ClientService.tsx'

export function ClientPage() {
    const {id} = useParams<string>()
    const {data, isPending, isError} = useGetClient(id || '')

    if (isError) return <p>error</p>
    if (isPending) return <p>loading</p>

    return (
        <div>
            <h1>Client</h1>
            <p>Name = {data.name}</p>
            <p>NIF = {data.nif}</p>
            <p>Phone = {data.phone}</p>
            <p>District = {data.location.district}</p>
        </div>

        // List of simulations
        // Create sim -> select service -> select price
        // Button to send to client
        // Button to generate link of sim
    )
}
