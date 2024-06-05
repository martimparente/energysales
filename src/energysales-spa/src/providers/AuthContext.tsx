// AuthContext.tsx
import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {authProvider} from './AuthProvider.tsx';

interface AuthContextType {
    auth: boolean | null;
    login: (variables: LoginVariables) => Promise<void>;
    logout: () => Promise<void>;
    checkError: (error: Error) => Promise<void>;
}

type LoginVariables = {
    username: string;
    password: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({children}) => {
    const [auth, setAuth] = useState<boolean | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        authProvider.checkAuth()
            .then(() => setAuth(true))
            .catch(() => setAuth(false));
    }, []);

    const login = async (variables: LoginVariables) => {
        try {
            const response = await authProvider.login(variables);
            if (response.success) {
                setAuth(true);
                navigate(response.redirectTo);
            } else {
                alert(response.error?.message);
                if (response.redirectOnFail) navigate(response.redirectOnFail);
            }
        } catch (error) {
            console.error('Login error', error);
        }
    };

    const logout = async () => {
        console.log('logoutC')
        await authProvider.logout();
        setAuth(false);
        navigate('/login');
    };

    const checkError = async (error: Error) => {
        console.log('checkError', error);
        await authProvider.checkError(error)
            .then(() => setAuth(true))
            .catch(() => {
                setAuth(false)
                navigate('/login')
            })
    }

    return (
        <AuthContext.Provider value={{auth, login, logout, checkError}}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
