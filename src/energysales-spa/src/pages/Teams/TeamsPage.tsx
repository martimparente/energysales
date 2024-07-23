import {useTeamsPage} from './useTeamsPage.tsx';
import {Team} from '../../services/models/TeamModel';
import {Box, Button, Card, Grid, Group, Stack, Text, Title} from "@mantine/core";
import {IconEdit, IconEye, IconPlus, IconTrash} from '@tabler/icons-react';

export function TeamsPage() {
    const {
        columns,
        teams,
        createTeam,
        updateTeam,
        deleteTeam,
        onShowClickHandler,
        managersCandidates,
        mappedManagersCandidates,
        onCreateTeamButtonClick,
        isFetching,
        error,
    } = useTeamsPage();

    // Function to get the email of the manager based on the full name
    const getEmailByManagerName = (name: string) => {
        const manager = managersCandidates?.find((manager) => `${manager.name} ${manager.surname}` === name);
        return manager ? manager.email : '';
    };

    return (
        <Box p="md">
            <Group position="apart" mb="lg" justify="space-between">
                <Title order={1}>Teams</Title>
                <Button onClick={() => onCreateTeamButtonClick()} color="green"
                        leftIcon={<IconPlus size={16}/>}>+</Button>
            </Group>

            <Grid>
                {teams?.map((team: Team) => (
                    <Grid.Col xs={12} sm={6} md={4} lg={3} key={team.id}>
                        <Card shadow="sm" padding="md">
                            <Group position="apart" align="flex-start" justify="space-between">
                                <Stack spacing="xs">
                                    <Title order={4}>{team.name}</Title>
                                    <Text color="dimmed">{team.location.district}</Text>
                                    <Text>{team.manager}</Text>
                                </Stack>
                                <Group direction="column" spacing="xs">
                                    <Button size="xs" onClick={() => onShowClickHandler(team)} color="blue"
                                            leftIcon={<IconEye size={16}/>}>Show</Button>
                                    <Button size="xs" onClick={console.log} color="orange"
                                            leftIcon={<IconEdit size={16}/>}>Edit</Button>
                                    <Button size="xs" onClick={() => deleteTeam(team)} color="red"
                                            leftIcon={<IconTrash size={16}/>}>Delete</Button>
                                </Group>
                            </Group>
                        </Card>
                    </Grid.Col>
                ))}
            </Grid>
        </Box>
    );
}