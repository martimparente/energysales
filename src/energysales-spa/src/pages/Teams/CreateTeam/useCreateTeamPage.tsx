import {useState} from 'react'
import {useForm} from 'react-hook-form'
import {CreateTeamInputModel} from '../../../services/models/TeamModel.tsx'
import {useCreateTeam, useGetAvailableSellers} from '../../../services/TeamsService.tsx'
import {useDebounce} from '@uidotdev/usehooks'

export function useCreateTeamPage() {
    const {control, handleSubmit} = useForm<CreateTeamInputModel>({
        defaultValues: {
            name: '',
            managerId: '',
            location: {
                district: ''
            }
        }
    })

    const portugalDistricts = [
        'Aveiro',
        'Beja',
        'Braga',
        'Bragança',
        'Castelo Branco',
        'Coimbra',
        'Évora',
        'Faro',
        'Guarda',
        'Leiria',
        'Lisboa',
        'Portalegre',
        'Porto',
        'Santarém',
        'Setúbal',
        'Viana do Castelo'
    ]
    const {mutateAsync: createTeam, isPending} = useCreateTeam()
    const [error, setError] = useState<string | null>(null)

    const [searchQuery, setSearchQuery] = useState('')
    const debouncedSearchQuery = useDebounce(searchQuery, 500)
    const {data: availableUsers} = useGetAvailableSellers(debouncedSearchQuery)

    return {
        availableUsers,
        portugalDistricts,
        control,
        handleSubmit,
        createTeam: async (input: CreateTeamInputModel) => await createTeam(input).catch(() => setError('error')),
        isPending,
        error
    }
}
