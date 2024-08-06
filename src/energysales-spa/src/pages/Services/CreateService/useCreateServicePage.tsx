import {useCreateService} from '../../../services/ServiceService.tsx'
import {CreateServiceInputModel} from '../../../services/models/ServiceModel'
import {useState} from 'react'

export function useCreateServicePage() {
    const {mutateAsync: createService} = useCreateService()
    const [isFetching, setIsFetching] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const createServiceHandler = async (input: CreateServiceInputModel) => {
        setIsFetching(true)
        try {
            await createService(input)
        } catch (e) {
            setError(e.message)
        } finally {
            setIsFetching(false)
        }
    }

    return {
        createService: createServiceHandler,
        isFetching,
        error
    }
}
