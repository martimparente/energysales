import { useQuery, } from '@tanstack/react-query'
import { useState, useEffect } from 'react';
import { Pagination, Table, Text } from '@mantine/core';
import MyTable from '../components/MyTable'

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
  const [activePage, setPage] = useState(1);
  const [lastKeySeen, setlastKeySeen] = useState(0);

  const fetchTeams = fetch(`http://localhost:8080/api/teams?lastKeySeen=${lastKeySeen}`, {
    method: 'GET', headers: {
      'Accept': 'application/json',
      'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU1MzgyNjJ9.YfP1L2LBuyx0jxjnz2KhrDhYXAu1srJwNeHm8DGMLXo"
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
  };

  return (
    <div>
      <h1>Teams</h1>
      <MyTable caption="team" head={tableData.head} body={tableData.body} />
      <Pagination total={data.length} value={activePage} onChange={setPage} mt="sm" />
    </div >
  )
}
