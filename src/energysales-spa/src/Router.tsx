import {createBrowserRouter, Navigate, Outlet, RouterProvider} from 'react-router-dom';
import {MainLayout} from './layouts/MainLayout'
import {HomePage} from './pages/HomePage';
import {TeamPage} from './pages/Teams/Team/TeamPage';
import {TeamsPage} from './pages/Teams/TeamsPage.tsx';
import {SellersPage} from './pages/Sellers/SellersPage.tsx';
import {SellerPage} from './pages/Sellers/Seller/SellerPage';
import {ProductsPage} from './pages/Products/ProductsPage.tsx';
import {ProductPage} from './pages/Products/Product/ProductPage.tsx';
import {LoginPage} from './pages/Auth/LoginPage.tsx';
import {ForgotPassword} from './pages/Auth/ForgotPasswordPage.tsx';
import {useIsAuthenticated} from "@refinedev/core";
import {ClientPage} from "./pages/Clients/Client/ClientPage.tsx";
import {ClientsPage} from "./pages/Clients/ClientsPage.tsx";

const PrivateRoutes = () => {
    const {isLoading, data} = useIsAuthenticated();

    if (isLoading) {
        return <p>Loading...</p>
    }

    if (data?.authenticated === false) {
        return <Navigate to="/login"/>
    } else return <Outlet/>
}

const routes = [
    {path: "/login", element: <LoginPage/>},
    {path: "/forgotpassword", element: <ForgotPassword/>},
    {
        element: <PrivateRoutes></PrivateRoutes>,
        children: [
            {path: "/", element: <HomePage/>},
            {path: "/teams", element: <TeamsPage/>},
            {path: "/teams/:id", element: <TeamPage/>},
            {path: "/sellers", element: <SellersPage/>},
            {path: "/sellers/:id", element: <SellerPage/>},
            {path: "/products", element: <ProductsPage/>},
            {path: "/products/:id", element: <ProductPage/>},
            {path: "/clients", element: <ClientsPage/>},
            {path: "/clients/:id", element: <ClientPage/>}
        ]
    }
]

const router = createBrowserRouter([
    {
        element: <MainLayout/>,
        children: [...routes],
    }
])

export function Router() {
    return <RouterProvider router={router}/>;
}
