import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/useAuth.tsx';

type ProtectedRouteProps = {
    roles?: string[];
}

export const ProtectedRoute = ({ roles }: ProtectedRouteProps) => {
    const location = useLocation();
    const { isLoggedIn, user } = useAuth();

    console.log('user', user);
    console.log('roles', roles);

    if (!isLoggedIn()) {
        return <Navigate to='/login' state={{ from: location }} replace />;
    }

    if (roles && !roles.includes(user?.role as string)) {
        throw new Error('Unauthorized');
    }

    return <Outlet />;
};