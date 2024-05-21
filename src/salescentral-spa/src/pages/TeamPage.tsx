import { useParams } from 'react-router-dom';
import { useGetTeam } from '../services/TeamsService';

export function TeamPage() {
  const { id } = useParams<string>();
  console.log(id);
  const { data, isPending, isError } = useGetTeam(id || '');

  if (isError) return <p>error</p>
  if (isPending) return <p>loading</p>

  return (
    <div>
      <h1>Team</h1>
      <p>Name = {data?.name}</p>
      <p>Location = {data?.location?.district}</p>
    </div >
  )
}
