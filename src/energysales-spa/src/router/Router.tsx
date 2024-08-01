import {createBrowserRouter} from 'react-router-dom'
import {HomePage} from '../pages/HomePage.tsx'
import {TeamPage} from '../pages/Teams/Team/TeamPage.tsx'
import {TeamsPage} from '../pages/Teams/TeamsPage.tsx'
import {UsersPage} from '../pages/Users/UsersPage.tsx'
import {UserPage} from '../pages/Users/User/UserPage.tsx'
import {ServicesPage} from '../pages/Services/ServicesPage.tsx'
import {ServicePage} from '../pages/Services/Service/ServicePage.tsx'
import {LoginPage} from '../pages/Auth/LoginPage.tsx'
import {ForgotPasswordPage} from '../pages/Auth/ForgotPasswordPage.tsx'
import {ClientPage} from '../pages/Clients/Client/ClientPage.tsx'
import {ClientsPage} from '../pages/Clients/ClientsPage.tsx'
import {SettingsPage} from '../pages/Settings/SettingsPage.tsx'
import {CreateServicePage} from '../pages/Services/CreateService/CreateServicePage.tsx'
import {CreateUserPage} from '../pages/Users/CreateUser/CreateUserPage.tsx'
import {CreateTeamPage} from '../pages/Teams/CreateTeam/CreateTeamPage.tsx'
import {CreateClientPage} from '../pages/Clients/CreateClient/CreateClientPage.tsx'
import {ProtectedRoute} from './ProtectedRoute.tsx'
import App from '../App.tsx'
import {ChangePasswordPage} from '../pages/Auth/ChangePasswordPage.tsx'
import {MakeOfferPage} from '../pages/Clients/MakeOffer/MakeOfferPage.tsx'
import {IconBrandAsana, IconBuilding, IconBulb, IconHome, IconSettings, IconUsersGroup} from '@tabler/icons-react'

/**
 ⚠️ Make sure the roles are the same as the protected routes in the Router
 ️ ️Otherwise the user will see the links but won't be able to access the pages
 */
export const navLinks = [
    {link: '/', label: 'Home', icon: IconHome, roles: ['ADMIN', 'MANAGER']},
    {link: '/teams', label: 'Teams', icon: IconBrandAsana, roles: ['ADMIN']},
    {link: '/users', label: 'Users', icon: IconUsersGroup, roles: ['ADMIN']},
    {link: '/services', label: 'Services', icon: IconBulb, roles: ['ADMIN']},
    {link: '/clients', label: 'Clients', icon: IconBuilding, roles: ['ADMIN', 'SELLER']},
    {link: '/settings', label: 'Settings', icon: IconSettings, roles: ['ADMIN', 'SELLER']}
]

export const router = createBrowserRouter([
    {
        element: <App/>,
        children: [
            {path: '/login', element: <LoginPage/>},
            {path: '/forgot-password', element: <ForgotPasswordPage/>},
            {path: '/change-password', element: <ChangePasswordPage/>},
            {
                element: <ProtectedRoute/>,
                children: [
                    {path: '/', element: <HomePage/>},
                    {path: '/settings', element: <SettingsPage/>}
                ]
            },
            {
                element: <ProtectedRoute role={'ADMIN'}/>,
                children: [
                    {path: '/teams', element: <TeamsPage/>},
                    {path: '/teams/:id', element: <TeamPage/>},
                    {path: '/teams/create', element: <CreateTeamPage/>},
                    {path: '/users', element: <UsersPage/>},
                    {path: '/users/:id', element: <UserPage/>},
                    {path: '/users/create', element: <CreateUserPage/>},
                    {path: '/services', element: <ServicesPage/>},
                    {path: '/services/:id', element: <ServicePage/>},
                    {path: '/services/create', element: <CreateServicePage/>},
                    {path: '/clients', element: <ClientsPage/>},
                    {path: '/clients/:id', element: <ClientPage/>},
                    {path: '/clients/create', element: <CreateClientPage/>},
                    {path: '/clients/:id/make-offer', element: <MakeOfferPage/>}
                ]
            },
            {
                element: <ProtectedRoute role={'SELLER'}/>,
                children: [
                    {path: '/clients', element: <ClientsPage/>},
                    {path: '/clients/:id', element: <ClientPage/>},
                    {path: '/clients/create', element: <CreateClientPage/>},
                    {path: '/clients/:id/make-offer', element: <MakeOfferPage/>}
                ]
            }
        ]
    }
])
