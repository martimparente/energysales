import {createBrowserRouter} from 'react-router-dom'
import {HomePage} from '../pages/HomePage.tsx'
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
import {CreateClientPage} from '../pages/Clients/CreateClient/CreateClientPage.tsx'
import {ProtectedRoute} from './ProtectedRoute.tsx'
import App from '../App.tsx'
import {ChangePasswordPage} from '../pages/Auth/ChangePasswordPage.tsx'
import {MakeOfferPage} from '../pages/Clients/MakeOffer/MakeOfferPage.tsx'
import {IconBrandAsana, IconBuilding, IconBulb, IconHome, IconSettings, IconUsersGroup} from '@tabler/icons-react'
import {TeamPage} from "../pages/Teams/Team/TeamPage.tsx";

export const routesConfig = [
    {path: '/', element: <HomePage/>, navLinkLabel: 'Home', icon: IconHome, roles: ['ADMIN', 'MANAGER']},
    {path: '/teams', element: <TeamsPage/>, navLinkLabel: 'Teams', icon: IconBrandAsana, roles: ['ADMIN']},
    {path: '/teams/:id', element: <TeamPage/>, roles: ['ADMIN']},
    {path: '/users', element: <UsersPage/>, navLinkLabel: 'Users', icon: IconUsersGroup, roles: ['ADMIN', 'MANAGER']},
    {path: '/users/:id', element: <UserPage/>, roles: ['ADMIN', 'MANAGER']},
    {path: '/users/create', element: <CreateUserPage/>, roles: ['ADMIN', 'MANAGER']},
    {
        path: '/services',
        element: <ServicesPage/>,
        navLinkLabel: 'Services',
        icon: IconBulb,
        roles: ['ADMIN', 'MANAGER']
    },
    {path: '/services/:id', element: <ServicePage/>, roles: ['ADMIN', 'MANAGER']},
    {path: '/services/create', element: <CreateServicePage/>, roles: ['ADMIN', 'MANAGER']},
    {
        path: '/clients',
        element: <ClientsPage/>,
        navLinkLabel: 'Clients',
        icon: IconBuilding,
        roles: ['MANAGER', 'SELLER']
    },
    {path: '/clients/:id', element: <ClientPage/>, roles: ['MANAGER', 'SELLER']},
    {path: '/clients/create', element: <CreateClientPage/>, roles: ['MANAGER', 'SELLER']},
    {path: '/clients/:id/make-offer', element: <MakeOfferPage/>, roles: ['MANAGER', 'SELLER']},
    {
        path: '/settings',
        element: <SettingsPage/>,
        navLinkLabel: 'Settings',
        icon: IconSettings,
        roles: ['ADMIN', 'MANAGER', 'SELLER']
    },
];


const generateProtectedRoutes = (routes) => {
    return routes.map(route => ({
        path: route.path,
        element: <ProtectedRoute allowedRoles={route.roles}>{route.element}</ProtectedRoute>
    }));
};

export const router = createBrowserRouter([
    {
        element: <App/>,
        children: [
            {path: '/login', element: <LoginPage/>},
            {path: '/forgot-password', element: <ForgotPasswordPage/>},
            {path: '/change-password', element: <ChangePasswordPage/>},
            ...generateProtectedRoutes(routesConfig),
        ],
    },
]);