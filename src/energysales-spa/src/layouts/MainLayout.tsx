import {ActionIcon, AppShell, Burger, Button, Code, Group, useMantineColorScheme} from '@mantine/core';
import {useDisclosure} from '@mantine/hooks';
import {Link, Outlet} from "react-router-dom";
import {useAuth} from "../context/useAuth.tsx";
import {NavLinks} from "./NavLinks.tsx";
import {IconMoonStars, IconSun} from '@tabler/icons-react';
import logoName from '../assets/logo+name.svg';
import {useEffect} from "react";

const energySalesIcon = <img src={logoName} width="150" height="50" alt="Logo"/>

export function MainLayout() {
    const [opened, {toggle, close}] = useDisclosure();
    const {colorScheme, toggleColorScheme} = useMantineColorScheme({
        keepTransitions: true,
    });
    const {user, logout} = useAuth();

    // close the navbar if the user is not logged in
    // close the navbar if the user is not logged in
    useEffect(() => {
        if (!user) {
            console.log(user + " user   useEffect   user is not logged in")
            close();
        }
    }, [user, close]);


    return (
        <AppShell
            header={{height: {base: 60, md: 70, lg: 80}}}
            navbar={{
                width: {base: 200, md: 300, lg: 400},
                breakpoint: 'sm',
                collapsed: {mobile: !opened},
            }}
            padding="md"
        >
            <AppShell.Header>
                <Group h="100%" px="md" justify="space-between">
                    <Group>
                        <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm"/>
                        {energySalesIcon}
                        <Code fw={700}>v0.1.0</Code>
                    </Group>

                    <Group>
                        {user ? (
                            <>
                                <span>Hi, {user.username}!</span>
                                <Button onClick={logout}>Logout</Button>
                            </>
                        ) : (
                            <Button component={Link} to="/login">Login</Button>
                        )}


                        <ActionIcon
                            onClick={() => toggleColorScheme()}
                            variant="transparent"
                            size="lg"
                            title="Toggle color scheme"
                        >
                            {colorScheme === 'dark' ? <IconSun size="1.25rem"/> : <IconMoonStars size="1.25rem"/>}
                        </ActionIcon>
                    </Group>
                </Group>
            </AppShell.Header>

            <AppShell.Navbar p="md">
                <NavLinks user={user}/>
            </AppShell.Navbar>

            <AppShell.Main>
                <Outlet/>
            </AppShell.Main>
        </AppShell>
    );
}