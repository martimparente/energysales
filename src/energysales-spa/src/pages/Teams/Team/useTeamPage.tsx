import {
    useAddTeamSeller,
    useDeleteTeam,
    useGetAvailableSellers,
    useGetTeamDetails,
    useUpdateTeam
} from '../../../services/TeamsService';
import {useNavigate, useParams} from "react-router-dom"
import {Team, UpdateTeamInputModel} from "../../../services/models/TeamModel";
import {useState} from "react";

export function useTeamPage() {

    const navigate = useNavigate();
    const {id} = useParams<string>();
    const {lastKeySeen} = useParams<{ lastKeySeen: string }>();
    const {data: teamDetails, isFetching, error} = useGetTeamDetails(id || '');
    const {data: availableSellers} = useGetAvailableSellers(lastKeySeen || "0");
    const {mutateAsync: addSeller} = useAddTeamSeller();
    const {mutateAsync: updateTeam} = useUpdateTeam();
    const {mutateAsync: deleteTeam} = useDeleteTeam();
    const [sellerId, setSellerId] = useState<string>("")


    const handleUpdateTeamForm = async (input: UpdateTeamInputModel) => await updateTeam(input)
    const handleDeleteTeam = async (team: Team) => await deleteTeam(team.id)
    const handleSelectSellerChange = (value: string) => setSellerId(value)
    const handleAddSelectSellerToTeam = () => addSeller({teamId: id, sellerId: sellerId})

    /*
        const handleSubmit = () => {
            if (sellerId == null || sellerId === "") {
                console.log("Please fill out all fields")
                return
            }
        }
    */

    return {
        teamDetails,
        addSeller: addSeller,
        updateTeam: handleUpdateTeamForm,
        deleteTeam: handleDeleteTeam,
        onShowClickHandler: (team: Team) => navigate(`/teams/${team.id}`),
        isFetching,
        error,
        availableSellers,
        handleSelectSellerChange,
        handleAddSelectSellerToTeam
    }
}