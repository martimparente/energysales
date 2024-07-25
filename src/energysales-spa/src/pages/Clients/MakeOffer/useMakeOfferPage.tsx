import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useGetServices } from "../../../services/ServicesService.tsx";
import { useGetClient, useMakeOffer } from "../../../services/ClientService.tsx";
import { useParams } from "react-router-dom";
import { MakeOfferInputModel } from "../../../services/models/ClientModel.tsx";

export function useMakeOfferPage() {
    const { id } = useParams<string>();
    const { control, handleSubmit } = useForm();
    const [isPending, setIsPending] = useState(false);
    const [error, setError] = useState(null);
    const [selectedService, setSelectedService] = useState<string | null>(null);
    const [expandedService, setExpandedService] = useState<string | null>(null);
    const [dueInDays, setDueInDays] = useState<number>(0);
    const [offerResponse, setOfferResponse] = useState<{ url: string, dueDate: string } | null>(null);

    const { data: client, error: fetchClientError } = useGetClient(id!);
    const { data: services, error: fetchError, isFetching } = useGetServices();
    const { mutateAsync: createOfferLink } = useMakeOffer();

    const makeOffer = async () => {
        setIsPending(true);
        setError(null);
        try {
            const offer: MakeOfferInputModel = {
                clientId: id!,
                serviceId: selectedService!,
                dueInDays: dueInDays
            };

            const response = await createOfferLink(offer);
            setOfferResponse(response);
        } catch (e) {
            setError(e.message);
        } finally {
            setIsPending(false);
        }
    };

    const sendOfferLinkByEmail = () => {
        if (!selectedService) return;
        // Handle logic to send offer link by email
    };

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
        sendOfferLinkByEmail,
        dueInDays,
        setDueInDays,
        offerResponse
    };
}