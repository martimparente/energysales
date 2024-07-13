import {createBrowserRouter} from 'react-router-dom';
import {MainLayout} from '../layouts/MainLayout.tsx'
import {HomePage} from '../pages/HomePage.tsx';
import {TeamPage} from '../pages/Teams/Team/TeamPage.tsx';
import {TeamsPage} from '../pages/Teams/TeamsPage.tsx';
import {UsersPage} from '../pages/Users/UsersPage.tsx';
import {UserPage} from '../pages/Users/User/UserPage.tsx';
import {ServicesPage} from '../pages/Services/ServicesPage.tsx';
import {ServicePage} from '../pages/Services/Service/ServicePage.tsx';
import {LoginPage} from '../pages/Auth/LoginPage.tsx';
import {ForgotPassword} from '../pages/Auth/ForgotPasswordPage.tsx';
import {ClientPage} from "../pages/Clients/Client/ClientPage.tsx";
import {ClientsPage} from "../pages/Clients/ClientsPage.tsx";
import {SettingsPage} from "../pages/Settings/SettingsPage.tsx";
import {ProtectedRouteAdmin} from "./ProtectedRouteAdmin.tsx";
import {ProtectedRouteManager} from "./ProtectedRouteManager.tsx";
import {AuthLayout} from "../providers/AuthLayout.tsx";
import {CreateServicePage} from "../pages/Services/CreateService/CreateServicePage.tsx";
import {CreateUserPage} from "../pages/Users/CreateUser/CreateUserPage.tsx";
import {CreateTeamPage} from "../pages/Teams/CreateTeam/CreateTeamPage.tsx";
import {
    IconBrandAsana,
    IconBuilding,
    IconBulb,
    IconHome,
    IconKey,
    IconLogin2,
    IconSettings,
    IconUsersGroup
} from "@tabler/icons-react";
/*
const PrivateRoutes = () => {
    const {isLoading, data} = useIsAuthenticated();

    if (isLoading) {
        return <p>Loading...</p>
    }

    if (data?.authenticated === false) {
        return <Navigate to="/login"/>
    } else return <Outlet/>
}*/

export const sidebarLinks = [
    {link: '/', label: 'Home', icon: IconHome, roles: ['admin', 'manager']},
    {link: '/login', label: 'Login', icon: IconLogin2, roles: ['admin', 'manager']},
    {link: '/forgot-password', label: 'Forgot Password', icon: IconKey, roles: ['admin', 'manager']},
    {link: '/teams', label: 'Teams', icon: IconBrandAsana, roles: ['admin']},
    {link: '/users', label: 'Users', icon: IconUsersGroup, roles: ['admin']},
    {link: '/services', label: 'Services', icon: IconBulb, roles: ['admin']},
    {link: '/clients', label: 'Clients', icon: IconBuilding, roles: ['admin', 'manager']},
    {link: '/settings', label: 'Settings', icon: IconSettings, roles: ['admin', 'manager']},
];

const routes = [
    {
        element: <MainLayout/>,
        children: [
            {path: "/login", element: <LoginPage/>},
            {path: "/forgot-password", element: <ForgotPassword/>},
            {
                element: <ProtectedRouteAdmin/>,
                children: [
                    {path: "/teams", element: <TeamsPage/>},
                    {path: "/teams/:id", element: <TeamPage/>},
                    {path: "/teams/create", element: <CreateTeamPage/>},
                    {path: "/users", element: <UsersPage/>},
                    {path: "/users/:id", element: <UserPage/>},
                    {path: "/users/create", element: <CreateUserPage/>},
                    {path: "/services", element: <ServicesPage/>},
                    {path: "/services/:id", element: <ServicePage/>},
                    {path: "/services/create", element: <CreateServicePage/>}
                ]
            },
            {
                element: <ProtectedRouteManager/>,
                children: [
                    {path: "/", element: <HomePage/>},
                    {path: "/clients", element: <ClientsPage/>},
                    {path: "/clients/:id", element: <ClientPage/>},
                    {path: "/settings", element: <SettingsPage/>}
                ]
            }]
    }]

export const router = createBrowserRouter([
    {
        element: <AuthLayout/>,
        children: [...routes],
    }
])