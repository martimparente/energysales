import {useParams} from 'react-router-dom'
import {useGetUser} from '../../../services/UsersService'

export function UserPage() {
    const {id} = useParams<string>()
    console.log(id)
    const {data, isPending, isError} = useGetUser(id || '')

    if (isError) return <p>error</p>
    if (isPending) return <p>loading</p>

    return (
        <div>
            <h1>User {data.id}</h1>
            <p>Name = {data.name}</p>
            <p>Surname = {data.surname}</p>
            <p>Email = {data.email}</p>
            <p>Role = {data.role}</p>
        </div>
    )
}
