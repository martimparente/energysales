import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuth} from "../context/useAuth.tsx";

type ProtectedRouteProps = {
    role?: string;
}

export const ProtectedRoute = ({role}: ProtectedRouteProps) => {
    const location = useLocation();
    const {isLoggedIn, user} = useAuth();

    if (!isLoggedIn()) {
        return <Navigate to="/login" state={{from: location}} replace/>;
    }

    if (role && user?.role !== role) {
        throw new Error('Unauthorized');
    }

    return <Outlet/>;
};