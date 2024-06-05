import {AuthProvider} from "./AuthContext";
import {Outlet} from "react-router-dom";
import React from "react";

export const AuthLayout: React.FC = () => {
    return (
        <AuthProvider>
            <Outlet/>
        </AuthProvider>
    );
};