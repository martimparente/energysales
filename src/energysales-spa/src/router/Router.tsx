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
import {ProtectedRouteSeller} from "./ProtectedRouteSeller.tsx";
import {AuthLayout} from "../providers/AuthLayout.tsx";
import {CreateServicePage} from "../pages/Services/CreateService/CreateServicePage.tsx";
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
                    {path: "/users", element: <UsersPage/>},
                    {path: "/users/:id", element: <UserPage/>},
                    {path: "/users/create", element: <CreateUsersPage/>}
                    {path: "/services", element: <ServicesPage/>},
                    {path: "/services/:id", element: <ServicePage/>},
                    {path: "/services/create", element: <CreateServicePage/>}
                ]
            },
            {
                element: <ProtectedRouteSeller/>,
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