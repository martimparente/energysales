import {Button, Group, PasswordInput, Select, TextInput} from '@mantine/core';
import {Controller} from 'react-hook-form';
import {useCreateUserPage} from './useCreateUserPage.tsx';

export function CreateUserPage() {
    const {
        control,
        handleSubmit,
        createUser,
        isPending,
        error,
    } = useCreateUserPage();

    return (
        <div>
            <h1>Create User</h1>
            <form onSubmit={handleSubmit(createUser)}>
                <Controller
                    name="username"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Username"
                            placeholder="Enter username"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="password"
                    control={control}
                    render={({field}) => (
                        <PasswordInput
                            label="Password"
                            placeholder="Enter password"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="repeatPassword"
                    control={control}
                    render={({field}) => (
                        <PasswordInput
                            label="Repeat Password"
                            placeholder="Repeat password"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="name"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="First Name"
                            placeholder="Enter first name"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="surname"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Surname"
                            placeholder="Enter surname"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="email"
                    control={control}
                    render={({field}) => (
                        <TextInput
                            label="Email"
                            placeholder="Enter email"
                            type="email"
                            {...field}
                            required
                        />
                    )}
                />
                <Controller
                    name="role"
                    control={control}
                    render={({field}) => (
                        <Select
                            label="Role"
                            placeholder="Select role"
                            data={['Admin', 'User']}
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