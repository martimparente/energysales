import {ApiUris} from "./ApiUris";
import {toast} from "react-toastify";

export interface LoginResponse {
    token: string;
}

export const loginAPI = async (username: string, password: string): Promise<LoginResponse | undefined> => {
    try {
        const response = await fetch(ApiUris.login, {
            method: "POST",
            body: JSON.stringify({username, password}),
            headers: {"Content-Type": "application/json"},
        });
        const data = await response.json();

        if (!(response.status >= 200 && response.status < 300)) {
            console.log("fetch failed")
            toast.warning(data.title || 'Something went wrong');
        }
        return data;
    } catch (error) {
        toast('Something went wrong');
    }
};

export const resetPasswordAPI = async (email: string): Promise<void> => {
    try {
        const response = await fetch(ApiUris.resetPassword, {
            method: "POST",
            body: JSON.stringify({email}),
            headers: {"Content-Type": "application/json"},
        });

        if (response.status == 200)
            toast.success("Verify your e-mail inbox for the password reset link")
        else {
            const data = await response.json();
            console.log("fetch failed")
            toast.error(data.title || 'Something went wrong');
        }
    } catch (error) {
        toast.error("Something went wrong")
    }
}
