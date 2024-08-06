import {Button, Group, Text, TextInput} from '@mantine/core'
import {Controller} from 'react-hook-form'
import {useCreatePartnerPage} from './useCreatePartnerPage.tsx'
import {Seller} from '../../../services/models/UserModel.tsx'
import {ReactSearchAutocomplete} from 'react-search-autocomplete'

export function CreatePartnerPage() {
    const {
        availableUsers,
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

    return (
        <div>
            <h1>Create Team</h1>
            <form onSubmit={handleSubmit(createTeam)}>
                <Controller
                    name='name'
                    control={control}
                    render={({field}) => <TextInput label='Team Name' placeholder='Enter partner name' {...field}
                                                    required/>}
                />
                <Controller
                    name='location.district'
                    control={control}
                    render={({field}) => <TextInput label='District' placeholder='Enter district' {...field} required/>}
                />

                <ReactSearchAutocomplete<Seller>
                    items={availableUsers}
                    onSearch={handleOnUsersSearch}
                    onSelect={handleOnUsersSelect}
                    formatResult={formatResult}
                />

                <Group mt='md'>
                    <Button type='submit'>Create</Button>
                </Group>
            </form>

            {isPending && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    )
}
