import { useQuery, } from '@tanstack/react-query'
import { useState, useEffect } from 'react';
import { Pagination, Table, Text } from '@mantine/core';
import { usePagination } from '@mantine/hooks';
import MyTable from '../components/MyTable'
import { Button } from '@mantine/core';
import Example from '../components/PaginationTable';

interface Team {
  id: number;
  name: number;
  manager: string;
  location: Location;
}

interface Location {
  district: string;
}

export function TeamsPage() {
/*   const [activePage, setPage] = useState(1);
  const [lastKeySeen, setlastKeySeen] = useState(0);
  const {
    data,
    isError,
    isFetching,
    isLoading,
  } = useGetTeams();

  function useGetTeams() {
    console.log('useGetTeams');
    return useQuery<Team[]>({
      queryKey: 'teams',
      queryFn: () => fetch(`http://localhost:8080/api/teams?lastKeySeen=0`, {
        headers: {
          'Accept': 'application/json',
          'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU4ODkzMTR9.Nqt2nFmBB3nm-k-14721JQwS7wBqQNSe1MCXsKbPqEo"
        }
      }).then((res) => res.json()),
    }
    )
  }

  if(isError) return console.log(data)
  if(data) return <p>{data[0].name}</p>
 */



  /* const { isPending, isError, data, error }  = useQuery<Team[]>({
    queryKey: ['teams'],
    queryFn: () => fetch(`http://localhost:8080/api/teams?lastKeySeen=0`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU4NjYwNDN9.p7YG89Ue9nBD2IYfs3utAxF1PPLw6bBRHyy3zbhsZDA"
      }
    }).then((res) => { console.log(res); return res.json() })
  }); */



  /* 
    const fetchTeams = fetch(`http://localhost:8080/api/teams?lastKeySeen=${lastKeySeen}`, {
      method: 'GET', headers: {
        'Accept': 'application/json',
        'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU4NjYwNDN9.p7YG89Ue9nBD2IYfs3utAxF1PPLw6bBRHyy3zbhsZDA"
      }
    }).then((res) => res.json())
  
    const { isLoading, error, data } = useQuery({
      queryKey: [activePage],
      queryFn: () => fetchTeams
    });
  
    useEffect(() => {
      if (data) {
        const lastItem = data[data.length - 1];
        setlastKeySeen(lastItem.id);
        console.log(lastItem.id)
        
      }
    }, [data]);
  
  
    if (isLoading) return 'Loading...'
    if (error) return 'An error has occurred: ' + error.message
  
    const tableData = {
      caption: 'Some Teams',
      head: ['Id', 'Name', 'Location', 'Manager'],
      body: data.map((team: Team) => (
        [team.id, team.name, team.location.district, team.manager])),
    }; */

  return (
    <div>
      <h1>Teams</h1>
      {/* <MyTable caption="team" head={tableData.head} body={tableData.body} /> */}
      {/* <Pagination total={data.length} value={activePage} /> */}
      <Example />
      {/* <Button onClick={() => pagination.setPage(5)} >Next</Button> */}
    </div >
  )
}
