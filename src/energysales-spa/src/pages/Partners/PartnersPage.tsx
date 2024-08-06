import {usePartnersPage} from './usePartnersPage.tsx'
import {Partner} from '../../services/models/TeamModel'
import {Box, Button, Group, SimpleGrid, Title} from '@mantine/core'
import {IconPlus} from '@tabler/icons-react'
import {PartnerCard} from '../../components/Cards/PartnerCard.tsx'
import {CreatePartnerDrawer} from '../../components/Drawers/CreatePartnerDrawer.tsx'
import {useState} from 'react'

export function PartnersPage() {
    const {partners, deleteTeam, onShowClickHandler, onCreateTeamButtonClick, isFetching, error} = usePartnersPage()

    const [drawerOpened, setDrawerOpened] = useState(false)

    return (
        <Box p='md'>
            <Group mb='lg' justify='space-between'>
                <Title>Partners</Title>
                <Button onClick={() => setDrawerOpened(true)} color='blue'>
                    <IconPlus size={16}/>
                </Button>
            </Group>

            <SimpleGrid cols={3}>
                {partners?.map((partner: Partner) => (
                    <Box onClick={() => onShowClickHandler(partner)} key={partner.name} style={{cursor: 'pointer'}}>
                        <PartnerCard partner={partner} onShowClickHandler={onShowClickHandler} deleteTeam={deleteTeam}/>
                    </Box>
                ))}
            </SimpleGrid>

            <CreatePartnerDrawer opened={drawerOpened} onClose={() => setDrawerOpened(false)}/>
        </Box>
    )
}
