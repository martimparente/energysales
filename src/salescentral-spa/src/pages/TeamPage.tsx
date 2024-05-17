import { useQuery, } from '@tanstack/react-query'
import { Routes, Route, useParams } from 'react-router-dom';

interface Team {
  id: number;
  name: number;
  manager: string;
  location: Location;
}

interface Location {
  district: string;
}

const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU5MDM3MjV9.1VCdT-gl8V1T5b1tGoNENMUtREhV6nSz1-pUK6GplEk"

export function TeamPage() {
  const { id } = useParams();
  const {
    data,
    isError,
    isFetching,
    isLoading,
  } = useQuery<Team>({
    queryKey: ['teams'],
    queryFn: () => fetch(`http://localhost:8080/api/teams/${id}`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': "Bearer " + token
      }
    }).then((res) => res.json()),
  })

  if (isError) return <p>error</p>
  if (isLoading) return <p>loading</p>

  return (
    <div>
      <h1>Team</h1>
      <p>Name = {data.name}</p>
      <p>Location = {data?.location.district}</p>



    </div >
  )
}
