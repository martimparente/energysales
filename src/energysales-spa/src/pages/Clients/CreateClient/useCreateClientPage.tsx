import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {CreateClientInputModel} from "../../../services/models/ClientModel.tsx";
import {useCreateClient} from "../../../services/ClientService.tsx";


export function useCreateClientPage() {
    const {control, handleSubmit} = useForm<CreateClientInputModel>({
        defaultValues: {
            name: '',
            nif: '',
            phone: '',
            location: {
                district: '',
            },
            sellerId: '',
        }
    });

    const {mutateAsync: createClient, isPending} = useCreateClient();
    const [error, setError] = useState<string | null>(null);

    return {
        control,
        handleSubmit,
        createClient: async (input: CreateClientInputModel) => await createClient(input).catch(() => setError("error")),
        isPending,
        error,
    };
}