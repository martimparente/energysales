import {useTeamsPage} from './useTeamsPage.tsx';
import {Team} from '../../services/models/TeamModel';
import {Box, Button, Grid, Group, SimpleGrid, Title} from "@mantine/core";
import {IconPlus} from '@tabler/icons-react';
import {TeamCard} from "../../components/Cards/TeamCard.tsx";

export function TeamsPage() {
    const {
        teams,
        deleteTeam,
        onShowClickHandler,
        onCreateTeamButtonClick,
        isFetching,
        error,
    } = useTeamsPage();

    return (
        <Box p="md">
            <Group mb="lg" justify="space-between">
                <Title order={1}>Teams</Title>
                <Button onClick={() => onCreateTeamButtonClick()} color="blue">
                    <IconPlus size={16}/>
                </Button>
            </Group>

            <SimpleGrid cols={3}>
                {teams?.map((team: Team) => (

                        <Box
                            onClick={() => onShowClickHandler(team)}
                            style={{cursor: 'pointer'}}
                        >
                            <TeamCard
                                key={team.name}
                                team={team}
                                onShowClickHandler={onShowClickHandler}
                                deleteTeam={deleteTeam}
                            />
                        </Box>

                ))}
            </SimpleGrid>
        </Box>
    );
}