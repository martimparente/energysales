import {useState} from 'react'
import {useForm} from 'react-hook-form'
import {CreateUserInputModel} from '../../../services/models/UserModel'
import {useCreateUser} from '../../../services/UserService.tsx'

export function useCreateUserPage() {
    const {control, handleSubmit} = useForm<CreateUserInputModel>({
        defaultValues: {
            username: '',
            password: '',
            repeatPassword: '',
            name: '',
            surname: '',
            email: '',
            role: ''
        }
    })

    const {mutateAsync: createUser, isPending} = useCreateUser()
    const [error, setError] = useState<string | null>(null)

    return {
        control,
        handleSubmit,
        createUser: async (input: CreateUserInputModel) => await createUser(input).catch(() => setError('error')),
        isPending,
        error
    }
}
