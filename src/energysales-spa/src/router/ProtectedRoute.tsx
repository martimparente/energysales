import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/useAuth.tsx';

export const ProtectedRoute = ( { children, allowedRoles } :{ children: React.ReactNode, allowedRoles?: string[] }) => {
    const location = useLocation();
    const { isLoggedIn, user } = useAuth();

    if (!isLoggedIn()) {
        return <Navigate to='/login' state={{ from: location }} replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user?.role as string)) {
        return <Navigate to='/' replace />;
    }

    return children;
};