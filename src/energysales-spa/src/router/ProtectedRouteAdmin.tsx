// ProtectedRoute.tsx

import {Outlet} from 'react-router-dom';

export const ProtectedRouteAdmin = () => {
    /*    const {auth} = useAuth();

        if (auth === null) {
            // Optionally, render a loading spinner while checking auth status
            return <div>Loading...</div>;
        }

        return auth ? <Outlet/> : <Navigate to="/login"/>;*/

    return <Outlet/>
};
