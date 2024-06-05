// ProtectedRoute.tsx

import {Navigate, Outlet} from 'react-router-dom';
import {useAuth} from '../providers/AuthContext';

export const ProtectedRouteSeller = () => {
    const {auth} = useAuth();

    if (auth === null) {
        // Optionally, render a loading spinner while checking auth status
        return <div>Loading...</div>;
    }

    return auth ? <Outlet/> : <Navigate to="/login"/>;
};


/*
import {useEffect} from "react";
import {Navigate, Outlet, useNavigate} from "react-router-dom";
import jwtDecode from "jwt-decode";


const ProtectedRouteSeller = (props) => {
/!*    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    function presentPage() {
        navigate(-1);
    }

    if (!token) return <Navigate to="/"/>;

    useEffect(() => {
        if (token && jwtDecode(token).role !== "admin") {
            presentPage()
        }
    }, [token && jwtDecode(token).role !== "admin"])

    const decodedData = jwtDecode(token);


    if (decodedData.role === "admin") {
        return <Outlet {...props} />;
    } else if (decodedData.role !== "admin") {
        presentPage()
    }*!/
};

export default ProtectedRouteSeller;*/
