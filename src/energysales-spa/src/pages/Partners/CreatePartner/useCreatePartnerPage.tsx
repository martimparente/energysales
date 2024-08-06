import {useState} from 'react'
import {useForm} from 'react-hook-form'
import {CreatePartnerInputModel} from '../../../services/models/TeamModel.tsx'
import {useCreatePartner, useGetAvailableSellers} from '../../../services/PartnerService.tsx'
import {useDebounce} from '@uidotdev/usehooks'

export function useCreatePartnerPage() {
    const {control, handleSubmit} = useForm<CreatePartnerInputModel>({
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
    const {mutateAsync: createTeam, isPending} = useCreatePartner()
    const [error, setError] = useState<string | null>(null)

    const [searchQuery, setSearchQuery] = useState('')
    const debouncedSearchQuery = useDebounce(searchQuery, 500)
    const {data: availableUsers} = useGetAvailableSellers(debouncedSearchQuery)

    return {
        availableUsers,
        portugalDistricts,
        control,
        handleSubmit,
        createTeam: async (input: CreatePartnerInputModel) => await createTeam(input).catch(() => setError('error')),
        isPending,
        error
    }
}
