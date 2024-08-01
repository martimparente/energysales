import {Avatar, Card, Group, Stack, Text, Title} from '@mantine/core'
import {Team} from '../../services/models/TeamModel.tsx'
import {ApiUris} from '../../services/ApiUris.tsx'

interface TeamCardProps {
    team: Team
}

export const TeamCard = ({team}: TeamCardProps) => {
    return (
        <Card shadow='sm' padding='lg' radius='md' withBorder>
            <Group align='flex-start' justify='space-between'>
                <Stack gap='xs'>
                    <Title order={4}>{team.name}</Title>
                    <Text c='dimmed'>{team.location.district}</Text>
                </Stack>

                <Avatar src={ApiUris.STATIC_RESOURCES_URL + team.avatarPath} key={team.id} color='yellow' size='lg'/>
            </Group>
        </Card>
    )
}
