import { useState } from 'react';
import { CreateUserInputModel } from '../../../services/models/UserModel';

export function useCreateUserPage() {
    const [isFetching, setIsFetching] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const createUser = async (input: CreateUserInputModel) => {
        setIsFetching(true);
        setError(null);

        try {
            // Replace with your actual API call logic
            await new Promise((resolve) => setTimeout(resolve, 1000));
            console.log('User created:', input);
        } catch (e) {
            setError('Failed to create user');
        } finally {
            setIsFetching(false);
        }
    };

    return {
        createUser,
        isFetching,
        error,
        onCreateUserButtonClick: () => {} // Placeholder, you can extend this as needed
    };
}