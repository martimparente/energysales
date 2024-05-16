import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import MainLayout from './layouts/MainLayout'
import { HomePage } from './pages/HomePage';
import { TeamsPage } from './pages/TeamsPage';
import { SellersPage } from './pages/SellersPage';

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
        path: "/teams",
        element: <TeamsPage />,
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
