import {Avatar, Card, Group, Stack, Text, Title} from '@mantine/core';
import {Team} from "../../services/models/TeamModel.tsx";

interface TeamCardProps {
    team: Team;
}

export const TeamCard = ({team}: TeamCardProps) => {
    return (
        <Card shadow="sm" padding="lg" radius="md" withBorder>
            <Group align="flex-start" justify="space-between">
                <Stack gap="xs">
                    <Title order={4}>{team.name}</Title>
                    <Text c="dimmed">{team.location.district}</Text>
                </Stack>
                <Avatar key={team.id} radius="xl" size="lg" />
            </Group>
        </Card>
    );
};