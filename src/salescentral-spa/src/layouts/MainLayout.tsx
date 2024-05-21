import { AppShell, Burger } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { Link, Outlet } from 'react-router-dom';
import {
        IconBellRinging,
        IconFingerprint,
        IconKey,
        IconSettings,
        Icon2fa,
        IconDatabaseImport,
        IconReceipt2,
        IconSwitchHorizontal,
        IconLogout
} from '@tabler/icons-react';

const sideBarSections = [
        { name: 'Home', url: '/' },
        { name: 'Login', url: '/login' },
        { name: 'Forgot Password', url: '/forgotpassword' },
        { name: 'Teams', url: '/teams' },
        { name: 'Sellers', url: '/sellers' }

];

const data = [
        { link: "", label: "Notifications", icon: IconBellRinging },
        { link: "", label: "Billing", icon: IconReceipt2 },
        { link: "", label: "Security", icon: IconFingerprint },
        { link: "", label: "SSH Keys", icon: IconKey },
        { link: "", label: "Databases", icon: IconDatabaseImport },
        { link: "", label: "Authentication", icon: Icon2fa },
        { link: "", label: "Other Settings", icon: IconSettings }
];

/* const links = data.map((item) => (
        <a
                href={item.link}
                key={item.label}
                onClick={(event) => {
                        event.preventDefault();
                        setActive(item.label);
                }}
        >
                <item.icon stroke={1.5} />
                <span>{item.label}</span>
        </a>
)); */


export function MainLayout() {
        const [opened, { toggle }] = useDisclosure();
        return (
                <div>
                        <AppShell
                                header={{ height: 60 }}
                                navbar={{
                                        width: 300,
                                        breakpoint: 'sm',
                                        collapsed: { mobile: !opened },
                                }}
                                padding="md"
                        >
                                <AppShell.Header>
                                        <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
                                        <img src="/src/assets/salescentral-logo+name.svg" width="300" height="75" />
                                </AppShell.Header>

                                <AppShell.Navbar p="md">
                                        {sideBarSections.map((section) => (
                                                <AppShell.Section key={section.name}>
                                                        <Link to={section.url} replace={true} >{section.name}</Link>
                                                </AppShell.Section>
                                        ))}
                                </AppShell.Navbar>

                                <AppShell.Main>
                                        <Outlet />
                                </AppShell.Main>

                        </AppShell>

                </div>
        );
}