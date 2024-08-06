import {Avatar, Card, Group, Stack, Text, Title} from '@mantine/core'
import {Partner} from '../../services/models/TeamModel.tsx'
import {ApiUris} from '../../services/ApiUris.tsx'

interface TeamCardProps {
    partner: Partner
}

export const PartnerCard = ({partner}: TeamCardProps) => {
    return (
        <Card shadow='sm' padding='lg' radius='md' withBorder>
            <Group align='flex-start' justify='space-between'>
                <Stack gap='xs'>
                    <Title order={4}>{partner.name}</Title>
                    <Text c='dimmed'>{partner.location.district}</Text>
                </Stack>

                <Avatar src={ApiUris.STATIC_RESOURCES_URL + partner.avatarPath} key={partner.id} color='yellow' size='lg'/>
            </Group>
        </Card>
    )
}
