import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiUris} from "./ApiUris";
import {CreateServiceInputModel, Service, UpdateServiceInputModel} from "./models/ServiceModel";
import {fetchData, mutateData} from "./ApiUtils.tsx";

export function useCreateService() {
    return useMutation({
        mutationFn: (input: CreateServiceInputModel) =>
            mutateData(ApiUris.createService, "POST", input),

        // TODO REDIRECT TO RESOURCE CREATED
    });
}

export function useGetServices(lastKeySeen: string = "0") {
    return useQuery<Service[]>({
        queryKey: ["services", lastKeySeen],
        queryFn: () => fetchData<Service[]>(ApiUris.getServices(lastKeySeen)),
    });
}

export function useGetService(id: string) {
    return useQuery<Service>({
        queryKey: [`service-${id}`],
        queryFn: () => fetchData<Service>(ApiUris.getService(id)),
    });
}

export function useUpdateService() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (input: UpdateServiceInputModel) =>
            mutateData(ApiUris.updateService(input.id), "PUT", input),
        onSuccess: (data, variables) => {
            // Assuming 'data' is the updated resource returned from the API
            // Update your cache or state here
            queryClient.setQueryData([`service-${variables.id}`], data);
        },
    });
}

export function useDeleteService() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id : string) =>
            mutateData(ApiUris.deleteService(id), "DELETE"),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['services']});
        },
    });
}
