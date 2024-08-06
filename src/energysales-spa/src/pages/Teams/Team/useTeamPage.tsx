import {
    useAddServiceToTeam,
    useAddTeamSeller,
    useDeleteServiceFromTeam,
    useDeleteTeam,
    useDeleteTeamSeller,
    useGetAvailableSellers,
    useGetTeamDetails,
    usePatchTeam,
    useUploadTeamAvatar
} from '../../../services/TeamsService'
import {useGetServices} from '../../../services/ServicesService.tsx'
import {useNavigate, useParams} from 'react-router-dom'
import {UpdateTeamInputModel} from '../../../services/models/TeamModel'
import {useState} from 'react'
import {useDebounce} from '@uidotdev/usehooks'
import {Seller, User} from '../../../services/models/UserModel.tsx'
import {Service} from '../../../services/models/ServiceModel.tsx'
import {useIsFetching} from '@tanstack/react-query'
import {modals} from '@mantine/modals'
import {AvatarUploadModal} from '../../../components/Modals/AvatarUploadModal.tsx'
import {AddTeamSellerModal} from '../../../components/Modals/AddTeamSellerModal.tsx'
import {Group, Text} from '@mantine/core'
import {AddTeamServiceModal} from '../../../components/Modals/AddTeamServiceModal.tsx'
import {toast} from 'react-toastify'

export function useTeamPage() {
    const navigate = useNavigate()
    const {id} = useParams<string>()

    const {data: teamDetails} = useGetTeamDetails(id || '')
    const [searchQuery, setSearchQuery] = useState('')
    const debouncedSearchQuery = useDebounce(searchQuery, 500)
    const {data: availableSellers} = useGetAvailableSellers(debouncedSearchQuery)
    const {data: availableServices} = useGetServices()

    const {mutateAsync: addSeller} = useAddTeamSeller()
    const {mutateAsync: deleteSeller} = useDeleteTeamSeller()
    const {mutateAsync: deleteServiceFromTeam} = useDeleteServiceFromTeam()
    const {mutateAsync: addService} = useAddServiceToTeam()
    const {mutateAsync: uploadAvatar} = useUploadTeamAvatar()

    const [selectedSeller, setSelectedSeller] = useState<string>('')
    const [selectedService, setSelectedService] = useState<string>('')
    const {mutateAsync: updateTeam} = usePatchTeam()
    const {mutateAsync: deleteTeam} = useDeleteTeam()
    const [error, setError] = useState<string | null>(null)
    const isFetching = useIsFetching()
    let avatarFile: File | null = null

    const formatResultSeller = (item: Seller) => {
        return (
            <Group gap='sm'>
                {/*<Avatar src={item.image} size={36} radius="xl" />*/}
                <div>
                    <Text size='sm'>{item.name}</Text>
                    <Text size='xs' opacity={0.5}>
                        {item.email}
                    </Text>
                </div>
            </Group>
        )
    }
    const formatResultServices = (item: Service) => {
        return (
            <Group gap='sm'>
                {/*<Avatar src={item.image} size={36} radius="xl" />*/}
                <div>
                    <Text size='sm'>{item.name}</Text>
                </div>
            </Group>
        )
    }

    const avatarUploadModal = () =>
        modals.openConfirmModal({
            title: 'Upload New Avatar',
            children: AvatarUploadModal({
                currAvatarUrl: teamDetails.team.avatarPath,
                onFileAdded: (file: File | null) => avatarFile = file,
                onAvatarDelete: () => {
                    throw new Error('Function not implemented.')
                }
            }),
            labels: {confirm: 'Save', cancel: 'Cancel'},
            onCancel: () => console.log('Cancel'),
            onConfirm: async () => {
                try {
                    if (avatarFile != null) {
                        await uploadAvatar({
                            teamId: id!,
                            input: {avatarImg: avatarFile}
                        })
                        toast.success('Avatar updated successfully')
                    }
                } catch (error) {
                    toast.error('Failed to update avatar. Try again later')
                }
            }
        })

    const addTeamSellerModal = () =>
        modals.openConfirmModal({
            title: 'Select Seller to Add to Team',
            children: (
                <AddTeamSellerModal
                    availableSellers={availableSellers}
                    handleOnSellerSearch={(string: string) => setSearchQuery(string)}
                    handleOnSellerSelect={(item: User) => setSelectedSeller(item.id)}
                    formatResult={formatResultSeller}
                />
            ),
            centered: true,
            labels: {confirm: 'Add to Team', cancel: 'Cancel'},
            onCancel: () => console.log('Cancel'),
            onConfirm: () => addSeller({teamId: id!, input: {sellerId: selectedSeller}})
        })
    const addTeamServiceModal = () =>
        modals.openConfirmModal({
            title: 'Select Service to Add to Team',
            children: (
                <AddTeamServiceModal
                    availableServices={availableServices}
                    handleOnServiceSearch={(string: string) => setSearchQuery(string)}
                    handleOnServiceSelect={(item: Service) => setSelectedService(item.id)}
                    formatResult={formatResultServices}
                />
            ),
            centered: true,
            labels: {confirm: 'Add to Team', cancel: 'Cancel'},
            onCancel: () => console.log('Cancel'),
            onConfirm: () => addService({teamId: id!, input: {serviceId: selectedService}})
        })
    const confirmDeleteModal = () =>
        modals.openConfirmModal({
            title: 'Delete Team',
            centered: true,
            children: (
                <Text size='sm'>
                    Are you sure you want to delete your profile? This action is destructive and you will have to
                    contact support to restore
                    your data.
                </Text>
            ),
            labels: {confirm: 'Delete Team', cancel: "No don't delete it"},
            confirmProps: {color: 'red'},
            onCancel: () => console.log('Cancel'),
            onConfirm: async () => {
                await deleteTeam(teamDetails!.team.id).catch(setError)
                navigate('/teams')
            }
        })

    const handleUpdateTeam = async (input: UpdateTeamInputModel) => {
        try {
            await updateTeam({id: id!, input: input})
            toast.success('Team updated successfully')
        } catch (error) {
            toast.error('Failed to update team. Try again later')
        }
    }
    /*

        const handleUploadAvatar = async (file: File | null) => {
            try {
                await uploadTeamAvatar(teamDetails.team.id, avatarFile);
            } catch (error) {
                console.log(error);
            }
        };
    */

    const handleAvatarDelete = async () => {
        console.log('delete avatar')
    }

    return {
        teamDetails,
        availableSellers,
        availableServices,
        updateTeam: async (input: UpdateTeamInputModel) => await updateTeam(input),
        handleOnDeleteSellerFromTeam: (sellerId: string) => deleteSeller({teamId: id!, sellerId: sellerId}),
        handleUpdateTeam,
        handleOnShowService: (serviceId: string) => navigate(`/services/${serviceId}`),
        handleOnDeleteServiceFromTeam: (serviceId: string) =>
            deleteServiceFromTeam({
                teamId: id!,
                serviceId: serviceId
            }),
        isFetching,
        error,
        avatarUploadModal,
        addTeamSellerModal,
        addTeamServiceModal,
        confirmDeleteModal
    }
}
