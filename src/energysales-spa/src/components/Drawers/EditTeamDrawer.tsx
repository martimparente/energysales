import React, {useEffect} from 'react'
import {Button, Drawer, Select, TextInput} from '@mantine/core'
import {Controller, useForm} from 'react-hook-form'
import {UpdateTeamInputModel} from '../../services/models/TeamModel.tsx'

interface EditTeamDrawerProps {
    opened: boolean
    onClose: () => void
    onSubmit: (data: UpdateTeamInputModel) => void
    initialData: UpdateTeamInputModel
}

const portugalDistricts = [
    'Aveiro',
    'Beja',
    'Braga',
    'Bragança',
    'Castelo Branco',
    'Coimbra',
    'Évora',
    'Faro',
    'Guarda',
    'Leiria',
    'Lisboa',
    'Portalegre',
    'Porto',
    'Santarém',
    'Setúbal',
    'Viana do Castelo'
]

export const EditTeamDrawer: React.FC<EditTeamDrawerProps> = ({opened, onClose, onSubmit, initialData}) => {
    const {control, handleSubmit, reset} = useForm<UpdateTeamInputModel>({
        defaultValues: initialData
    })

    useEffect(() => {
        reset(initialData)
    }, [initialData, reset])

    const handleFormSubmit = (data: UpdateTeamInputModel) => {
        onSubmit(data)
        onClose()
    }

    return (
        <Drawer opened={opened} onClose={onClose} title='Edit Team' padding='sm' size='sm' position='right'>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
                <Controller name='name' control={control}
                            render={({field}) => <TextInput label='Team Name' {...field} mb='sm'/>}/>
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
                <Controller name='managerId' control={control}
                            render={({field}) => <TextInput label='Manager' {...field} mb='sm'/>}/>
                <Button type='submit' color='blue'>
                    Save Changes
                </Button>
            </form>
        </Drawer>
    )
}
