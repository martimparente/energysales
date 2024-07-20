import {toast} from "react-toastify";

export const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + localStorage.getItem("token"),
};

export async function fetchData<T>(url: string): Promise<T> {

    const response = await fetch(url, {headers: AUTHORIZATION_HEADER})
    const data = await response.json();

    if (!(response.status >= 200 && response.status < 300)) {
        console.log("fetch failed")
        toast.warning(data.title || 'Something went wrong');
    }
    return data;
}

export async function mutateData<T>(url: string, method: string, body?: any): Promise<T> {
    const response = await fetch(url, {
        method,
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(body),
    });
    const data = await response.json();

    if (!(response.status >= 200 && response.status < 300)) {
        console.log("mutate failed")
        toast.warning(data.title || 'Something went wrong');
    }
    return data;
}