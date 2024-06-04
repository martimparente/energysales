import {
    useAddTeamSeller,
    useDeleteTeam,
    useDeleteTeamSeller,
    useGetAvailableSellers,
    useGetTeamDetails,
    useUpdateTeam
} from '../../../services/TeamsService';
import {useNavigate, useParams} from "react-router-dom"
import {Team, UpdateTeamInputModel} from "../../../services/models/TeamModel";
import {useState} from "react";

export function useTeamPage() {

    const navigate = useNavigate();
    const {id} = useParams<string>()
    const {lastKeySeen} = useParams<{ lastKeySeen: string }>();
    const {data: teamDetails, isLoading: isLoadingTeamDetails} = useGetTeamDetails(id || '');
    const {data: availableSellers,} = useGetAvailableSellers(lastKeySeen || "0");
    const {mutateAsync: addSeller} = useAddTeamSeller();
    const {mutateAsync: deleteSeller} = useDeleteTeamSeller();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();
    const [sellerId, setSellerId] = useState<string>("")


    const handleUpdateTeamForm = async (input: UpdateTeamInputModel) => await updateTeam(input)
    const handleDeleteTeam = async (team: Team) => await deleteTeam(team.id)
    const handleSelectSellerChange = (value: string) => setSellerId(value)
    const handleAddSelectSellerToTeam = () => addSeller({teamId: id!, sellerId: sellerId})
    const handleDeleteSellerFromTeam = (sellerId: string) => deleteSeller({teamId: id!, sellerId: sellerId})

    return {
        teamDetails,
        isLoadingTeamDetails,
        addSeller: addSeller,
        updateTeam: handleUpdateTeamForm,
        deleteTeam: handleDeleteTeam,
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        availableSellers,
        handleSelectSellerChange,
        handleAddSelectSellerToTeam,
        handleDeleteSellerFromTeam,
    }
}