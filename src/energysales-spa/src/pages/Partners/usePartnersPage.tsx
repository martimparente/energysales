import {useDeletePartner, useGetManagerCandidates, useGetPartners} from '../../services/PartnerService'
import {useNavigate} from 'react-router-dom'
import {Partner} from '../../services/models/TeamModel'
import {useState} from 'react'
import {ManagerInfo} from '../../services/models/UserModel.tsx'

export function usePartnersPage() {
    const navigate = useNavigate()

    const {data: partners, error: fetchError, isFetching} = useGetPartners()
    const {mutateAsync: deleteTeam} = useDeletePartner()
    const {data: managersCandidates} = useGetManagerCandidates()
    const [error, setError] = useState<string | null>()

    if (fetchError && !error) {
        setError(fetchError.message)
    }

    /*    const mappedManagersCandidates: Record<string, { email: string }> = managersCandidates?.reduce((acc, manager) => {
            const fullName = `${manager.name} ${manager.surname}`;
            acc[fullName] = {
                email: manager.email,
            };
            return acc;
        }, {} as Record<string, { email: string }>) || {};*/

    const mappedManagersCandidates = managersCandidates?.map((manager: ManagerInfo) => `${manager.name} ${manager.surname}`)

    return {
        partners,
        deleteTeam: async (partner: Partner) => await deleteTeam(partner.id).catch((e) => setError(e.message)),
        onShowClickHandler: (partner: Partner) => navigate(`/partners/${partner.id}`),
        managersCandidates,
        mappedManagersCandidates,
        onCreateTeamButtonClick: () => navigate(`/partners/create`),
        isFetching,
        error
    }
}
