// authProvider.ts
export interface AuthProvider {
    login: (input: LoginVariables) => Promise<LoginResponse>;
    logout: () => Promise<any>;
    checkAuth: () => Promise<any>;
    checkError: (error: any) => Promise<any>;
    getIdentity: () => Promise<any>;
    getRoles: () => Promise<any>;
}

type LoginVariables = {
    username: string;
    password: string;
}

export interface LoginResponse {
    success: boolean;
    redirectTo: string;
    error?: { name: string; message: string };
    redirectOnFail?: string;
}

export const authProvider: AuthProvider = {
    // Implementation of the authProvider methods...
    login: async (input) => {
        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            body: JSON.stringify(input),
            headers: {"Content-Type": "application/json"},
        });
        const data = await response.json();

        if (data.token) {
            localStorage.setItem('token', data.token);
            return {
                success: true,
                redirectTo: "/",
                error: undefined,
                redirectOnFail: "",
            };
        } else {
            return {
                success: false,
                error: {name: "Login Error", message: "Invalid credentials"},
                redirectOnFail: "/login",
                redirectTo: "",
            };
        }
    },
    logout: async () => {
        console.log('logoutprovider')
        localStorage.removeItem("token");
        return Promise.resolve();
    },
    checkAuth: () => {
        return localStorage.getItem('token') ? Promise.resolve() : Promise.reject();
    },
    checkError: (error) => {
        const status = error.status;
        if (status === 401 || status === 403) {
            localStorage.removeItem('token');
            return Promise.reject();
        }
        return Promise.resolve();
    },
    getIdentity: () => Promise.resolve({id: 'user', role: 'admin'}),
    getRoles: () => Promise.resolve([]),
};
