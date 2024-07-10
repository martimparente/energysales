import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {CreateTeamInputModel} from "../../../services/models/TeamModel.tsx";
import {useCreateTeam} from "../../../services/TeamsService.tsx";


export function useCreateTeamPage() {
    const {control, handleSubmit} = useForm<CreateTeamInputModel>({
        defaultValues: {
            name: '',
            managerId: '',
            location: {
                district: '',
            },
        }
    });

    const {mutateAsync: createTeam, isPending} = useCreateTeam();
    const [error, setError] = useState<string | null>(null);

    return {
        control,
        handleSubmit,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(() => setError("error")),
        isPending,
        error,
    };
}