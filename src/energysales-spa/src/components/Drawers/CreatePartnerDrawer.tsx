import React from 'react'
import {Button, Drawer, Group, Select, Text, TextInput} from '@mantine/core'
import {Controller} from 'react-hook-form'
import {ReactSearchAutocomplete} from 'react-search-autocomplete'
import {useCreatePartnerPage} from '../../pages/Partners/CreatePartner/useCreatePartnerPage.tsx'
import {Seller} from "../../services/models/UserModel.tsx";

interface CreateTeamDrawerProps {
    opened: boolean
    onClose: () => void
}

export const CreatePartnerDrawer: React.FC<CreateTeamDrawerProps> = ({opened, onClose}) => {
    const {
        availableUsers,
        portugalDistricts,
        handleOnUsersSearch,
        handleOnUsersSelect,
        control,
        handleSubmit,
        createTeam,
        isPending,
        error
    } = useCreatePartnerPage()

    const formatResult = (item: Seller) => {
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

    const handleFormSubmit = async (data) => {
        await createTeam(data)
        onClose()
    }

    return (
        <Drawer opened={opened} onClose={onClose} title='Create Team' padding='sm' size='sm' position='right'>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
                <Controller
                    name='name'
                    control={control}
                    render={({field}) => <TextInput label='Team Name' placeholder='Enter partner name' {...field} required
                                                    mb='sm'/>}
                />
                <Controller
                    name='location.district'
                    control={control}
                    render={({field}) => (
                        <Select
                            label='District'
                            placeholder='Select district'
                            data={portugalDistricts.map((district) => ({
                                value: district,
                                label: district
                            }))}
                            {...field}
                            required
                            mb='sm'
                        />
                    )}
                />

                <ReactSearchAutocomplete<Seller>
                    items={availableUsers}
                    onSearch={handleOnUsersSearch}
                    onSelect={handleOnUsersSelect}
                    formatResult={formatResult}
                />

                <Group mt='md'>
                    <Button type='submit' color='blue'>
                        Create
                    </Button>
                </Group>
            </form>

            {isPending && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </Drawer>
    )
}
