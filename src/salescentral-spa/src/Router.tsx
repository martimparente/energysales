import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import MainLayout from './layouts/MainLayout'
import { HomePage } from './pages/HomePage';
import { TeamPage } from './pages/TeamPage';
import { TeamsPage } from './pages/TeamsPage';
import { SellersPage } from './pages/SellersPage';
import { LoginPage } from './pages/LoginPage';
import { ForgotPassword } from './pages/ForgotPasswordPage';
import { Authenticated } from "@refinedev/core";

const router = createBrowserRouter([
  {
    path: "/",
    element: <MainLayout />,
    children: [
      {
        path: "/",
        element: <HomePage />,
      },
      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/forgotpassword",
        element: <ForgotPassword />,
      },
      {
        path: "/teams",
        element: <Authenticated key="teams222"><TeamsPage /></Authenticated>,
      },
      {
        path: "/teams/:id",
        element: <TeamPage />,
      },
      {
        path: "/sellers",
        element: <SellersPage />,
      },
    ]
  },

]);

export function Router() {
  return <RouterProvider router={router} />;
}
