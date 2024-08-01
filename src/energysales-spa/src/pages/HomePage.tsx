import logoName from '../assets/logo+name.svg'
import {Box, Title} from '@mantine/core'

export function HomePage() {
    return (
        <Box p='md'>
            <Title>Home</Title>
            <img src={logoName}/>
        </Box>
    )
}
