import {createBrowserRouter} from 'react-router-dom';
import {HomePage} from '../pages/HomePage.tsx';
import {PartnersPage} from '../pages/Partners/PartnersPage.tsx';
import {UsersPage} from '../pages/Users/UsersPage.tsx';
import {UserPage} from '../pages/Users/User/UserPage.tsx';
import {ServicesPage} from '../pages/Services/ServicesPage.tsx';
import {ServicePage} from '../pages/Services/Service/ServicePage.tsx';
import {LoginPage} from '../pages/Auth/LoginPage.tsx';
import {ForgotPasswordPage} from '../pages/Auth/ForgotPasswordPage.tsx';
import {ClientPage} from '../pages/Clients/Client/ClientPage.tsx';
import {ClientsPage} from '../pages/Clients/ClientsPage.tsx';
import {SettingsPage} from '../pages/Settings/SettingsPage.tsx';
import {CreateServicePage} from '../pages/Services/CreateService/CreateServicePage.tsx';
import {CreateUserPage} from '../pages/Users/CreateUser/CreateUserPage.tsx';
import {CreateClientPage} from '../pages/Clients/CreateClient/CreateClientPage.tsx';
import {ProtectedRoute} from './ProtectedRoute.tsx';
import App from '../App.tsx';
import {ChangePasswordPage} from '../pages/Auth/ChangePasswordPage.tsx';
import {MakeOfferPage} from '../pages/Clients/MakeOffer/MakeOfferPage.tsx';
import {IconBrandAsana, IconBuilding, IconBulb, IconHome, IconSettings, IconUsersGroup} from '@tabler/icons-react';
import {PartnerPage} from "../pages/Partners/Partner/PartnerPage.tsx";
import {ReactNode} from "react";

interface Route {
    path: string;
    page: ReactNode;
    navLinkInfo?: NavLinkInfo;
    roles: string[];
}

interface NavLinkInfo {
    label: string;
    icon: any;
}

export const routesConfig: Route[] = [
    {
        path: '/',
        page: <HomePage/>,
        navLinkInfo: {label: 'Home', icon: IconHome},
        roles: ['ADMIN', 'MANAGER'],
    },
    {
        path: '/partners',
        page: <PartnersPage/>,
        navLinkInfo: {label: 'Partners', icon: IconBrandAsana},
        roles: ['ADMIN'],
    },
    {
        path: '/partners/:id',
        page: <PartnerPage/>,
        roles: ['ADMIN']
    },
    {
        path: '/users',
        page: <UsersPage/>,
        navLinkInfo: {label: 'Users', icon: IconUsersGroup},
        roles: ['ADMIN', 'MANAGER'],
    },
    {
        path: '/users/:id',
        page: <UserPage/>,
        roles: ['ADMIN', 'MANAGER']
    },
    {
        path: '/users/create',
        page: <CreateUserPage/>,
        roles: ['ADMIN', 'MANAGER']
    },
    {
        path: '/services',
        page: <ServicesPage/>,
        navLinkInfo: {label: 'Services', icon: IconBulb},
        roles: ['ADMIN', 'MANAGER'],
    },
    {
        path: '/services/:id',
        page: <ServicePage/>,
        roles: ['ADMIN', 'MANAGER']
    },
    {
        path: '/services/create',
        page: <CreateServicePage/>,
        roles: ['ADMIN', 'MANAGER']
    },
    {
        path: '/clients',
        page: <ClientsPage/>,
        navLinkInfo: {label: 'Clients', icon: IconBuilding},
        roles: ['MANAGER', 'SELLER'],
    },
    {
        path: '/clients/:id',
        page: <ClientPage/>,
        roles: ['MANAGER', 'SELLER']
    },
    {
        path: '/clients/create',
        page: <CreateClientPage/>,
        roles: ['MANAGER', 'SELLER']
    },
    {
        path: '/clients/:id/make-offer',
        page: <MakeOfferPage/>,
        roles: ['MANAGER', 'SELLER']
    },
    {
        path: '/settings',
        page: <SettingsPage/>,
        navLinkInfo: {label: 'Settings', icon: IconSettings},
        roles: ['ADMIN', 'MANAGER', 'SELLER'],
    },
];

const generateProtectedRoutes = (routes: Route[]) =>
    routes.map((route) => ({
        path: route.path,
        element: <ProtectedRoute allowedRoles={route.roles}>{route.page}</ProtectedRoute>,
    }));

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