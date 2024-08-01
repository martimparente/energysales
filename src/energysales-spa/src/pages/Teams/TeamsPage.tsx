import {useTeamsPage} from './useTeamsPage.tsx'
import {Team} from '../../services/models/TeamModel'
import {Box, Button, Group, SimpleGrid, Title} from '@mantine/core'
import {IconPlus} from '@tabler/icons-react'
import {TeamCard} from '../../components/Cards/TeamCard.tsx'
import {CreateTeamDrawer} from '../../components/Drawers/CreateTeamDrawer.tsx'
import {useState} from 'react'

export function TeamsPage() {
    const {teams, deleteTeam, onShowClickHandler, onCreateTeamButtonClick, isFetching, error} = useTeamsPage()

    const [drawerOpened, setDrawerOpened] = useState(false)

    return (
        <Box p='md'>
            <Group mb='lg' justify='space-between'>
                <Title>Teams</Title>
                <Button onClick={() => setDrawerOpened(true)} color='blue'>
                    <IconPlus size={16}/>
                </Button>
            </Group>

            <SimpleGrid cols={3}>
                {teams?.map((team: Team) => (
                    <Box onClick={() => onShowClickHandler(team)} style={{cursor: 'pointer'}}>
                        <TeamCard key={team.name} team={team} onShowClickHandler={onShowClickHandler}
                                  deleteTeam={deleteTeam}/>
                    </Box>
                ))}
            </SimpleGrid>

            <CreateTeamDrawer opened={drawerOpened} onClose={() => setDrawerOpened(false)}/>
        </Box>
    )
}
