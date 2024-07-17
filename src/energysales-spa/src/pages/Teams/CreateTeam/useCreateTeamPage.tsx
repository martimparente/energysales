import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {CreateTeamInputModel} from "../../../services/models/TeamModel.tsx";
import {useCreateTeam, useGetAvailableSellers} from "../../../services/TeamsService.tsx";
import {useDebounce} from "@uidotdev/usehooks";


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

    const [searchQuery, setSearchQuery] = useState("");
    const debouncedSearchQuery = useDebounce(searchQuery, 500);
    const {data: availableUsers} = useGetAvailableSellers(debouncedSearchQuery);


    return {
        availableUsers,
        control,
        handleSubmit,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(() => setError("error")),
        isPending,
        error,
    };
}