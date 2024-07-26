import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {CreateClientInputModel} from "../../../services/models/ClientModel.tsx";
import {useCreateClient} from "../../../services/ClientService.tsx";
import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";

export function useCreateClientPage() {
    const navigate = useNavigate();
    const {control, handleSubmit} = useForm<CreateClientInputModel>({
        defaultValues: {
            name: '',
            nif: '',
            phone: '',
            location: {
                district: '',
            }
        }
    });

    const {mutateAsync: createClient, isPending} = useCreateClient();
    const [error, setError] = useState<string | null>(null);

    return {
        control,
        handleSubmit,
        createClient: async (input: CreateClientInputModel) => {
            await createClient(input);
            toast.success('Client created successfully');
            navigate('/clients');
        },
        isPending,
        error,
    };
}