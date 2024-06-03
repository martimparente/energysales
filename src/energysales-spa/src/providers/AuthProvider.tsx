import {AuthProvider} from "@refinedev/core";

// https://github.com/refinedev/refine/blob/master/examples/auth-mantine/src/App.tsx
export const authProvider: AuthProvider = {
    login: async ({username, password}) => {
        const data = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            body: JSON.stringify({username, password}),
            headers: {"Content-Type": "application/json", "Accept": "application/json"},
        }).then((res) => res.json());

        if (data) {
            localStorage.setItem('token', data.token);
            return {
                success: true,
                redirectTo: "/",
                successNotification: {
                    message: "Login Successful",
                    description: "You have successfully logged in.",
                }
            };
        } else {
            return {
                success: false,
                error: {name: "Login Error", message: "Invalid credentials"},
                key: ["TODO"],
                redirectOnFail: "/login",
            };
        }
    },
    check: async () => {
        console.log("AUTH CHECKING")
        const token = localStorage.getItem("token");
        if (token) {
            console.log("✅ AUTH TOKEN = ", token)
            return {
                authenticated: true,
                redirectTo: "/",
            }
        } else {
            console.log("AUTH NO TOKEN")
            return {
                authenticated: false,
                error: {
                    message: "Check failed",
                    name: "Not authenticated",
                },
                logout: true,
                redirectTo: "/login",
            };
        }
    },
    logout: async () => {
        localStorage.removeItem("token");
        return {
            success: true,
            redirectTo: "/login",
        };
    },

    forgotPassword: async ({email}) => {
        // send password reset link to the user's email address
        const res = await fetch("http://localhost:8080/api/auth/reset-password", {
            method: "POST",
            body: JSON.stringify({email}),
            headers: {"Content-Type": "application/json", "Accept": "application/json"},
        })
        if (res.status == 200) {
            return {
                success: true,
                redirectTo: "/login",
            }
        }
        return {
            success: false,
            error: {
                name: "Forgot Password Error",
                message: "Email address does not exist",
            },
        }
    },
    onError: async (error) => {
        console.log("AUTH ERROR")
        const status = error.status;
        if (status !== 200) {
            return {
                logout: true,
                redirectTo: "/login",
                error: new Error(error),
            };
        }
        return {};
    },
    updatePassword: async (params) => ({
        success: false,
        error: {name: "Login Error", message: "Invalid credentials"},
    }),
};


