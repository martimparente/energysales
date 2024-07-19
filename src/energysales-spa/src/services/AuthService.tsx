import {ApiUris} from "./ApiUris";


export const loginAPI = async (username: string, password: string) => {
    try {
        return await fetch(ApiUris.login, {
            method: "POST",
            body: JSON.stringify({username, password}),
            headers: {"Content-Type": "application/json"},
        }).then((response) => response.json());
    } catch (error) {
        console.log(error);
    }
};
