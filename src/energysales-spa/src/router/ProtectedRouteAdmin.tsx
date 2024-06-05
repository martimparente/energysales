// ProtectedRoute.tsx

import {Navigate, Outlet} from 'react-router-dom';
import {useAuth} from '../providers/AuthContext';

export const ProtectedRouteAdmin = () => {
    const {auth} = useAuth();

    if (auth === null) {
        // Optionally, render a loading spinner while checking auth status
        return <div>Loading...</div>;
    }

    return auth ? <Outlet/> : <Navigate to="/login"/>;
};
