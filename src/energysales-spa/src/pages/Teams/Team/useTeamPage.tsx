import {
    useAddServiceToTeam,
    useAddTeamSeller,
    useDeleteServiceFromTeam,
    useDeleteTeam,
    useDeleteTeamSeller,
    useGetAvailableSellers,
    useGetTeamDetails,
    useUpdateTeam,
} from '../../../services/TeamsService';

import {useGetServices} from '../../../services/ServicesService.tsx';
import {useNavigate, useParams} from "react-router-dom"
import {Team, UpdateTeamInputModel} from "../../../services/models/TeamModel";
import {useState} from "react";
import {useDebounce} from "@uidotdev/usehooks";
import {User} from "../../../services/models/UserModel.tsx";
import {Service} from "../../../services/models/ServiceModel.tsx";


export function useTeamPage() {

    const navigate = useNavigate();
    const {id} = useParams<string>()

    const [searchQuery, setSearchQuery] = useState("");
    const debouncedSearchQuery = useDebounce(searchQuery, 500);
    const {data: availableSellers} = useGetAvailableSellers(debouncedSearchQuery);
    const {data: availableServices} = useGetServices();

    const {data: teamDetails} = useGetTeamDetails(id || '');
    const {mutateAsync: addSeller} = useAddTeamSeller();
    const {mutateAsync: deleteSeller} = useDeleteTeamSeller();
    const {mutateAsync: deleteServiceFromTeam} = useDeleteServiceFromTeam();

    const {mutateAsync: addService} = useAddServiceToTeam();


    const [selectedSeller, setSelectedSeller] = useState<string>("")
    const [selectedService, setSelectedService] = useState<string>("")
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();
    const [isPending, setIsPending] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleUpdateTeam = async (input: UpdateTeamInputModel) => {
        setIsPending(true);
        try {

            await updateTeam({...input, id: id!});
            setIsPending(false);
        } catch (error) {
            setError("Failed to update team");
            setIsPending(false);
        }
    };

    return {
        teamDetails,
        availableSellers,
        availableServices,
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input),
        handleOnDeleteTeam: async (team: Team) => await deleteTeam(team.id),
        handleOnSellerSearch: (string: string) => setSearchQuery(string),
        handleOnSellerSelect: (item: User) => setSelectedSeller(item.id),
        handleOnAddSellerToTeam: () => addSeller({teamId: id!, sellerId: selectedSeller}),
        handleOnDeleteSellerFromTeam: (sellerId: string) => deleteSeller({teamId: id!, sellerId: sellerId}),
        handleUpdateTeam,
        handleOnShowService: (serviceId: string) => navigate(`/services/${serviceId}`),
        handleOnAddServiceToTeam: () => addService({teamId: id!, serviceId: selectedService}),
        handleOnDeleteServiceFromTeam: (serviceId: string) => deleteServiceFromTeam({
            teamId: id!,
            serviceId: serviceId
        }),
        handleOnServiceSelect: (item: Service) => setSelectedService(item.id),
        isPending,
        error,
    }
}