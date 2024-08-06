import {useState} from 'react'
import {useForm} from 'react-hook-form'
import {useGetServices} from '../../../services/ServiceService.tsx'
import {useGetClient, useMakeOffer, useSendOfferEmail} from '../../../services/ClientService.tsx'
import {useParams} from 'react-router-dom'
import {MakeOfferInputModel} from '../../../services/models/ClientModel.tsx'
import {toast} from 'react-toastify'

export function useMakeOfferPage() {
    const {id: clientId} = useParams<string>()
    const {control, handleSubmit} = useForm()
    const [isPending, setIsPending] = useState(false)
    const [error, setError] = useState(null)
    const [selectedService, setSelectedService] = useState<string | null>(null)
    const [expandedService, setExpandedService] = useState<string | null>(null)
    const [dueInDays, setDueInDays] = useState<number>(0)
    const [offerResponse, setOfferResponse] = useState<{
        url: string
        dueDate: string
    } | null>(null)

    const {data: client, error: fetchClientError} = useGetClient(clientId!)
    const {data: services, error: fetchError, isFetching} = useGetServices()
    const {mutateAsync: createOfferLink} = useMakeOffer()
    const {mutateAsync: sendOfferEmail} = useSendOfferEmail()

    const makeOffer = async () => {
        setIsPending(true)
        setError(null)
        try {
            const offer: MakeOfferInputModel = {
                clientId: clientId!,
                serviceId: selectedService!,
                dueInDays: dueInDays
            }

            const response = await createOfferLink(offer)
            setOfferResponse(response)
            toast.success('Offer created successfully')
        } catch (e) {
            setError(e.message)
        } finally {
            setIsPending(false)
        }
    }

    const handleSendOfferEmail = async () => {
        if (!selectedService) return
        // Handle logic to send offer link by email
        try {
            await sendOfferEmail(clientId!)
            toast.success(`Success - Link send to ${client?.email}`)
        } catch (e) {
            setError(e.message)
        }
    }

    return {
        control,
        handleSubmit,
        isPending,
        error,
        client,
        services,
        selectedService,
        setSelectedService,
        expandedService,
        setExpandedService,
        handlerMakeOffer: makeOffer,
        handleSendOfferEmail,
        dueInDays,
        setDueInDays,
        offerResponse
    }
}
