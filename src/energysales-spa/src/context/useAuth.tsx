import React, {createContext, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {loginAPI} from "../services/AuthService.tsx";
import {UserProfile} from "../services/models/UserModel.tsx";
import {jwtDecode} from "jwt-decode";
import {toast} from "react-toastify";

type AuthContextType = {
    user: UserProfile | null;
    token: string | null;
    registerUser: (email: string, username: string, password: string) => void;
    loginUser: (username: string, password: string) => void;
    logout: () => void;
    isLoggedIn: () => boolean;
};

interface TokenClaims {
    username: string;
    userId: string;
    role: string;
}

const UserContext = createContext<AuthContextType>({} as AuthContextType);

export const AuthProvider = ({children}: { children: React.ReactNode }) => {
    const navigate = useNavigate();
    const [token, setToken] = useState<string | null>(null);
    const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
    const [isReady, setIsReady] = useState(false);

    useEffect(() => {
        const user = localStorage.getItem("user");
        const token = localStorage.getItem("token");
        if (user && token) {
            setUserProfile(JSON.parse(user));
            setToken(token);
        }
        setIsReady(true);
    }, []);


    const loginUser = async (username: string, password: string) => {
        await loginAPI(username, password)
            .then((res) => {
                if (res) {
                    const token = res.token;
                    localStorage.setItem("token", res?.token);

                    const decodedToken: TokenClaims = jwtDecode(token);
                    const {username, userId, role} = decodedToken;
                    const userProfile: UserProfile = {username, userId, role};

                    localStorage.setItem("user", JSON.stringify(userProfile));
                    setToken(token);
                    setUserProfile(userProfile);
                    navigate("/");
                    toast.success(`Welcome back, ${userProfile.username}!`, {position: "bottom-right"});
                }
            })
            .catch((e) => console.log(e));
    };

    const isLoggedIn = () => !!userProfile


    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUserProfile(null);
        setToken("");
        navigate("/login");
        toast.success(`Goodbye!`, {position: "bottom-right"})
    };

    return (
        <UserContext.Provider value={{loginUser, user: userProfile, token, logout, isLoggedIn}}>
            {isReady ? children : null}
        </UserContext.Provider>
    );
};

export const useAuth = () => React.useContext(UserContext);