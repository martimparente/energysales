import {Button, Group, TextInput} from '@mantine/core';
import {Controller} from 'react-hook-form';
import {useCreateClientPage} from './useCreateClientPage.tsx';

export function CreateClientPage() {
    const {
        control,
        handleSubmit,
        createClient,
        isPending,
        error,
    } = useCreateClientPage();

    return (
        <div>
            <h1>Create Client</h1>
            <form onSubmit={handleSubmit(createClient)}>
                <Controller
                    name="name"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Client Name"
                            placeholder="Enter Client name"
                            {...field}
                            required
                        />
                    )}
                />

                <Controller
                    name="nif"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Client NIF"
                            placeholder="Enter Client NIF"
                            {...field}
                            required
                        />
                    )}
                />

                <Controller
                    name="phone"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Client Phone Number"
                            placeholder="Enter Client Phone Number"
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

                <Group mt="md">
                    <Button type="submit">Create</Button>
                </Group>
            </form>

            {isPending && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    );
}