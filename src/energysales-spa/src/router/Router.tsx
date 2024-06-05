import {createBrowserRouter} from 'react-router-dom';
import {MainLayout} from '../layouts/MainLayout.tsx'
import {HomePage} from '../pages/HomePage.tsx';
import {TeamPage} from '../pages/Teams/Team/TeamPage.tsx';
import {TeamsPage} from '../pages/Teams/TeamsPage.tsx';
import {SellersPage} from '../pages/Sellers/SellersPage.tsx';
import {SellerPage} from '../pages/Sellers/Seller/SellerPage.tsx';
import {ProductsPage} from '../pages/Products/ProductsPage.tsx';
import {ProductPage} from '../pages/Products/Product/ProductPage.tsx';
import {LoginPage} from '../pages/Auth/LoginPage.tsx';
import {ForgotPassword} from '../pages/Auth/ForgotPasswordPage.tsx';
import {ClientPage} from "../pages/Clients/Client/ClientPage.tsx";
import {ClientsPage} from "../pages/Clients/ClientsPage.tsx";
import {SettingsPage} from "../pages/Settings/SettingsPage.tsx";
import {ProtectedRouteAdmin} from "./ProtectedRouteAdmin.tsx";
import {ProtectedRouteSeller} from "./ProtectedRouteSeller.tsx";
import {AuthLayout} from "../providers/AuthLayout.tsx";
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
                    {path: "/sellers", element: <SellersPage/>},
                    {path: "/sellers/:id", element: <SellerPage/>},
                    {path: "/products", element: <ProductsPage/>},
                    {path: "/products/:id", element: <ProductPage/>}
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