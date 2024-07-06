import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useGetService, useUpdateService, useDeleteService } from '../../../services/ServicesService';
import { Service, UpdateServiceInputModel } from '../../../services/models/ServiceModel';

export function useServicePage() {
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();

    const { data: service, isLoading: isLoadingService } = useGetService(id || '');
    const { mutateAsync: updateService } = useUpdateService();
    const { mutateAsync: deleteService } = useDeleteService();

    const [error, setError] = useState<string | null>(null);
    const [editableService, setEditableService] = useState<UpdateServiceInputModel>();

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;
        if (editableService) {
            if (name in editableService.price) {
                setEditableService({
                    ...editableService,
                    price: { ...editableService.price, [name]: parseFloat(value) }
                });
            } else {
                setEditableService({ ...editableService, [name]: value });
            }
        }
    };

    return {
        service,
        onUpdateServiceClick: () => {
            updateService(editableService!)
                .then(() => setEditableService(undefined))
                .catch(e => setError(e));
        },
        onDeleteServiceClick: (service: Service) => {
            deleteService(service.id)
                .then(() => navigate('/services'))
                .catch(e => setError(e));
        },
        onEditServiceButtonClick: () => {
            setEditableService(
                {
                    id: service!.id,
                    name: service!.name,
                    description: service!.description,
                    cycleName: service!.cycleName,
                    cycleType: service!.cycleType,
                    periodName: service!.periodName,
                    periodNumPeriods: service!.periodNumPeriods,
                    price: { ...service!.price }
                } as UpdateServiceInputModel
            )
        },
        isLoadingService,
        error,
        editableService,
        handleInputChange,
    };
}