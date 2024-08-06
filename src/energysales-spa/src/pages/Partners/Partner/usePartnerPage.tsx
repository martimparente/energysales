import {useGetServices} from '../../../services/ServiceService.tsx'
import {useNavigate, useParams} from 'react-router-dom'
import {UpdatePartnerInputModel} from '../../../services/models/TeamModel'
import {useState} from 'react'
import {useDebounce} from '@uidotdev/usehooks'
import {Seller, User} from '../../../services/models/UserModel.tsx'
import {Service} from '../../../services/models/ServiceModel.tsx'
import {useIsFetching} from '@tanstack/react-query'
import {modals} from '@mantine/modals'
import {AvatarUploadModal} from '../../../components/Modals/AvatarUploadModal.tsx'
import {AddPartnerSellerModal} from '../../../components/Modals/AddPartnerSellerModal.tsx'
import {Group, Text} from '@mantine/core'
import {AddPartnerServiceModal} from '../../../components/Modals/AddPartnerServiceModal.tsx'
import {toast} from 'react-toastify'
import {
    useAddPartnerSeller,
    useAddPartnerService,
    useDeletePartner,
    useDeletePartnerSeller,
    useDeletePartnerService,
    useGetAvailableSellers,
    useGetPartnerDetails,
    usePatchPartner,
    useUploadPartnerAvatar
} from "../../../services/PartnerService.tsx";

export function usePartnerPage() {
    const navigate = useNavigate()
    const {id} = useParams<string>()

    const {data: partnerDetails} = useGetPartnerDetails(id || '')
    const [searchQuery, setSearchQuery] = useState('')
    const debouncedSearchQuery = useDebounce(searchQuery, 500)
    const {data: availableSellers} = useGetAvailableSellers(debouncedSearchQuery)
    const {data: availableServices} = useGetServices()

    const {mutateAsync: addSeller} = useAddPartnerSeller()
    const {mutateAsync: deleteSeller} = useDeletePartnerSeller()
    const {mutateAsync: deleteServiceFromTeam} = useDeletePartnerService()
    const {mutateAsync: addService} = useAddPartnerService()
    const {mutateAsync: uploadAvatar} = useUploadPartnerAvatar()

    const [selectedSeller, setSelectedSeller] = useState<string>('')
    const [selectedService, setSelectedService] = useState<string>('')
    const {mutateAsync: updateTeam} = usePatchPartner()
    const {mutateAsync: deleteTeam} = useDeletePartner()
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
                currAvatarUrl: partnerDetails.partner.avatarPath,
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
                            partnerId: id!,
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
            title: 'Select Seller to Add to Partner',
            children: (
                <AddPartnerSellerModal
                    availableSellers={availableSellers}
                    handleOnSellerSearch={(string: string) => setSearchQuery(string)}
                    handleOnSellerSelect={(item: User) => setSelectedSeller(item.id)}
                    formatResult={formatResultSeller}
                />
            ),
            centered: true,
            labels: {confirm: 'Add to Partner', cancel: 'Cancel'},
            onCancel: () => console.log('Cancel'),
            onConfirm: () => addSeller({partnerId: id!, input: {sellerId: selectedSeller}})
        })
    const addTeamServiceModal = () =>
        modals.openConfirmModal({
            title: 'Select Service to Add to Partner',
            children: (
                <AddPartnerServiceModal
                    availableServices={availableServices}
                    handleOnServiceSearch={(string: string) => setSearchQuery(string)}
                    handleOnServiceSelect={(item: Service) => setSelectedService(item.id)}
                    formatResult={formatResultServices}
                />
            ),
            centered: true,
            labels: {confirm: 'Add to Partner', cancel: 'Cancel'},
            onCancel: () => console.log('Cancel'),
            onConfirm: () => addService({partnerId: id!, input: {serviceId: selectedService}})
        })
    const confirmDeleteModal = () =>
        modals.openConfirmModal({
            title: 'Delete Partner',
            centered: true,
            children: (
                <Text size='sm'>
                    Are you sure you want to delete your profile? This action is destructive and you will have to
                    contact support to restore
                    your data.
                </Text>
            ),
            labels: {confirm: 'Delete Partner', cancel: "No don't delete it"},
            confirmProps: {color: 'red'},
            onCancel: () => console.log('Cancel'),
            onConfirm: async () => {
                await deleteTeam(partnerDetails!.partner.id).catch(setError)
                navigate('/partners')
            }
        })

    const handleUpdateTeam = async (input: UpdatePartnerInputModel) => {
        try {
            await updateTeam({id: id!, input: input})
            toast.success('Partner updated successfully')
        } catch (error) {
            toast.error('Failed to update partner. Try again later')
        }
    }
    /*

        const handleUploadAvatar = async (file: File | null) => {
            try {
                await uploadTeamAvatar(partnerDetails.partner.id, avatarFile);
            } catch (error) {
                console.log(error);
            }
        };
    */

    const handleAvatarDelete = async () => {
        console.log('delete avatar')
    }

    return {
        partnerDetails,
        availableSellers,
        availableServices,
        updateTeam: async (input: UpdatePartnerInputModel) => await updateTeam(input),
        handleOnDeleteSellerFromTeam: (sellerId: string) => deleteSeller({partnerId: id!, sellerId: sellerId}),
        handleUpdateTeam,
        handleOnShowService: (serviceId: string) => navigate(`/services/${serviceId}`),
        handleOnDeleteServiceFromTeam: (serviceId: string) =>
            deleteServiceFromTeam({
                partnerId: id!,
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
