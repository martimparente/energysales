import { createBrowserRouter, RouterProvider, Outlet } from 'react-router-dom';
import { MainLayout } from './layouts/MainLayout'
import { HomePage } from './pages/HomePage';
import { TeamPage } from './pages/Teams/Team/TeamPage';
import { TeamsPage } from './pages/Teams/Teams';
import { SellersPage } from './pages/SellersPage';
import { LoginPage } from './pages/LoginPage';
import { ForgotPassword } from './pages/ForgotPasswordPage';
import { Authenticated } from "@refinedev/core";

const routes = [
  { path: "/", element: <HomePage />, },
  { path: "/login", element: <LoginPage />, },
  { path: "/forgotpassword", element: <ForgotPassword /> },
  { path: "/teams", element: <Authenticated key="teams222"><TeamsPage /></Authenticated>, },
  { path: "/teams/:id", element: <TeamPage /> },
  { path: "/sellers", element: <SellersPage />, }
]

const router = createBrowserRouter([
  {
    element: <>
      <MainLayout />
      <Outlet /> {/* An <Outlet> should be used in parent route elements to render their child route elements. */}
    </>,
    children: [...routes],
  }
])

export function Router() {
  return <RouterProvider router={router} />;
}
