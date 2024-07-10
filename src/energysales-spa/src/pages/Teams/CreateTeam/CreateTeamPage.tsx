import {Button, Group, NumberInput, TextInput} from '@mantine/core';
import {Controller} from 'react-hook-form';
import {useCreateTeamPage} from './useCreateTeamPage.tsx';

export function CreateTeamPage() {
    const {
        control,
        handleSubmit,
        createTeam,
        isPending,
        error,
    } = useCreateTeamPage();

    return (
        <div>
            <h1>Create Team</h1>
            <form onSubmit={handleSubmit(createTeam)}>
                <Controller
                    name="name"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Team Name"
                            placeholder="Enter team name"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="location.district"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="District"
                            placeholder="Enter district"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="managerId"
                    control={control}
                    render={({field}) => (
                        <NumberInput
                            label="Manager ID"
                            placeholder="Enter manager ID"
                            {...field}
                        />
                    )}
                />
                <Group mt="md">
                    <Button type="submit">Create</Button>
                </Group>
            </form>

            {isPending && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    );
}