import {toast} from "react-toastify";

export const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + localStorage.getItem("token"),
};

export async function fetchData<T>(url: string): Promise<T> {
    const response = await fetch(url, {headers: AUTHORIZATION_HEADER})
    const data = await response.json()
    if (!response.ok) {
        toast.warning(data.title || 'Something went wrong')
        throw new Error(data.title)
    }
    return data
}

export async function mutateData<T>(url: string, method: string, body?: any): Promise<T> {
    const response = await fetch(url, {
        method,
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(body),
    });

    if (!response.ok) {
        const problem = await response.json()
        toast.warning(problem.title || 'Something went wrong')
    }
    // only get json if content type is json
    if (response.headers.get('Content-Type')?.includes('application/json'))
        return await response.json()
}