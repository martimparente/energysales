import { AppShell, Burger } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { Link } from 'react-router-dom';
import { Outlet } from 'react-router-dom';

const sideBarSections = [
         { name: 'Home', url: '/' },
         { name: 'Login', url: '/login' },
         { name: 'Forgot Password', url: '/forgotpassword' },
         { name: 'Teams', url: '/teams' },
         { name: 'Sellers', url: '/sellers' }
         
     ];

export default function MainLayout() {
         const [opened, { toggle }] = useDisclosure();
         return (
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
                                    <div>Logo</div>
                                    <img src="./react.svg"/>
                           </AppShell.Header>

                           <AppShell.Navbar p="md">
                                    {sideBarSections.map((section) => (
                                             <AppShell.Section key={section.name}>
                                                     <Link to={section.url} replace={true} >{section.name}</Link>
                                             </AppShell.Section>
                                    ))}
                           </AppShell.Navbar>

                           <AppShell.Main><Outlet /></AppShell.Main>
                  </AppShell>

         );


}