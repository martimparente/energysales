import {CreateUserInputModel} from '../../../services/models/UserModel'
import {Button, Group, TextInput} from "@mantine/core"
import React, {useState} from "react"
import {useCreateUserPage} from "./useCreateUserPage.tsx"

export function CreateUserPage() {
    const {
        createUser,
        isFetching,
        error,
    } = useCreateUserPage();

    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [email, setEmail] = useState('');
    const [team, setTeam] = useState('');

    const handleCreateUser = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const input: CreateUserInputModel = {
            name: name,
            surname: surname,
            email: email,
            team: team
        };
        await createUser(input);
        setName('');
        setSurname('');
        setEmail('');
        setTeam('');
    };

    return (
        <div>
            <h1>Create User</h1>
            <form onSubmit={(e) => handleCreateUser(e)}>
                <TextInput
                    label="First Name"
                    placeholder="Enter first name"
                    value={name}
                    onChange={(e) => setName(e.currentTarget.value)}
                    required
                />
                <TextInput
                    label="Surname"
                    placeholder="Enter surname"
                    value={surname}
                    onChange={(e) => setSurname(e.currentTarget.value)}
                    required
                />
                <TextInput
                    label="Email"
                    placeholder="Enter email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.currentTarget.value)}
                    required
                />
                <TextInput
                    label="Team"
                    placeholder="Enter team"
                    value={team}
                    onChange={(e) => setTeam(e.currentTarget.value)}
                    required
                />
                <Group mt="md">
                    <Button type="submit">Create</Button>
                </Group>
            </form>

            {isFetching && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    );
}