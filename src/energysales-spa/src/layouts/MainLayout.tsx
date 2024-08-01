import {
    ActionIcon,
    AppShell,
    Avatar,
    Burger,
    Card,
    Code,
    Group,
    NavLink,
    Space,
    Stack,
    Text,
    useMantineColorScheme,
    useMantineTheme
} from '@mantine/core'
import {useDisclosure} from '@mantine/hooks'
import {Link, Outlet} from 'react-router-dom'
import {useAuth} from '../context/useAuth.tsx'
import {IconLogin, IconLogout, IconMoonStars, IconSun} from '@tabler/icons-react'
import logoName from '../assets/logo+name.svg'
import {useEffect, useState} from 'react'
import {navLinks} from '../router/Router.tsx'

const energySalesIcon = <img src={logoName} width='150' height='50' alt='Logo'/>

export function MainLayout() {
    const {colorScheme, toggleColorScheme} = useMantineColorScheme({
        keepTransitions: true
    })
    const theme = useMantineTheme()
    const [opened, {toggle, close}] = useDisclosure()
    const [active, setActive] = useState(0)
    const {user, logout} = useAuth()

    // close the navbar if the user is not logged in
    useEffect(() => {
        if (!user) {
            close()
        }
    }, [user, close])

    // print the active link
    useEffect(() => {
        console.log(active + navLinks[active].label)
    }, [active])

    return (
        <AppShell
            layout='alt'
            header={{height: {base: 50, md: 50, lg: 50}}}
            navbar={{
                width: {base: 250, md: 250, lg: 250},
                breakpoint: 'sm',
                collapsed: {mobile: !opened}
            }}
            padding='xs'
        >
            <AppShell.Header>
                <Group h='100%' px='md' justify='right'>
                    <Burger opened={opened} onClick={toggle} hiddenFrom='sm' size='sm'/>
                    <Group>
                        <ActionIcon onClick={() => toggleColorScheme()} variant='transparent' size='lg'
                                    title='Toggle color scheme'>
                            {colorScheme === 'dark' ? <IconSun size='1.25rem'/> : <IconMoonStars size='1.25rem'/>}
                        </ActionIcon>
                    </Group>
                </Group>
            </AppShell.Header>

            <AppShell.Navbar p='md'>
                <Stack gap='sm'>
                    <Group justify='space-around'>
                        {energySalesIcon}
                        <Code>v0.1.0</Code>
                    </Group>
                    {user && (
                        <Card padding='lg' radius='md' style={{backgroundColor: theme.colors.lime[9]}}>
                            <Group align='center'>
                                <Avatar radius='xl' src={user.avatar} alt={user.username}/>
                                <Text c='white'>Hi, {user.username}!</Text>
                            </Group>
                        </Card>
                    )}
                    <Space h='xl'/>
                    {navLinks
                        .filter((item) => item.roles.includes(user?.role || 'NOUSER'))
                        .map((item, index) => (
                            <NavLink
                                to={item.link}
                                label={item.label}
                                key={item.label}
                                leftSection={<item.icon/>}
                                component={Link}
                                active={index === active}
                                onClick={() => setActive(index)}
                                variant='subtle'
                            />
                        ))}
                    {user ? (
                        <NavLink leftSection={<IconLogout/>} label={'Logout'} onClick={logout}/>
                    ) : (
                        <NavLink to='/login' label='Login' leftSection={<IconLogin/>} component={Link}/>
                    )}
                </Stack>
            </AppShell.Navbar>

            <AppShell.Main>
                <Outlet/>
            </AppShell.Main>
        </AppShell>
    )
}
