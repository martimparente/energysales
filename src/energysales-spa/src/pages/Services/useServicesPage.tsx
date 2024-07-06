import {useCreateService, useDeleteService, useGetServices, useUpdateService} from '../../services/ServicesService';
import {useNavigate} from "react-router-dom";
import {CreateServiceInputModel, Service, UpdateServiceInputModel} from '../../services/models/ServiceModel';
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useServicesPage() {
    const navigate = useNavigate();

    const {data: services, error: fetchError, isFetching} = useGetServices();
    const {mutateAsync: createService} = useCreateService();
    const {mutateAsync: updateService} = useUpdateService();
    const {mutateAsync: deleteService} = useDeleteService();

    const [error, setError] = useState<string | null>(null);


    const columns: Column[] = [
        {accessor: 'name', header: 'Name', sortable: true},
        {accessor: 'cycleName', header: 'Cycle Name', sortable: true},
        {accessor: 'cycleType', header: 'Cycle Type', sortable: true},
        {accessor: 'description', header: 'Description', sortable: true},
        {accessor: 'actions', header: 'Action', sortable: false},
    ];

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    return {
        columns,
        services,
        createService: async (input: CreateServiceInputModel) => await createService(input).catch(e => setError(e)),
        onEditServiceClick: (input: UpdateServiceInputModel) => {
            updateService(input)
                .then(() => console.log('Service updated'))
                .catch(e => setError(e));
        },
        onDeleteServiceClick: (service: Service) => {
            deleteService(service.id)
                .then(() => console.log('Service deleted'))
                .catch(e => setError(e));
        },
        onShowServiceButtonClick: (service: Service) => navigate(`/services/${service.id}`),
        onCreateServiceButtonClick: () => navigate(`/services/create`),
        isFetching,
        error,
    }
}