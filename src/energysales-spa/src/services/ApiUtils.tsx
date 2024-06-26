export const AUTHORIZATION_HEADER = {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + localStorage.getItem("token"),
};

export async function fetchData<T>(url: string): Promise<T> {
    const response = await fetch(url, {headers: AUTHORIZATION_HEADER})
    if (!(response.status >= 200 && response.status < 300)) {
        const errorData = await response.json();
        console.log("ERROR")
        throw new Error(errorData.message || 'Something went wrong');
    }
    return response.json();
}

export async function mutateData<T>(url: string, method: string, body?: any): Promise<T> {
    const response = await fetch(url, {
        method,
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(body),
    });
    if (!(response.status >= 200 && response.status < 300)) {
        const errorData = await response.json();
        console.log("ERROR")

        throw new Error(errorData.message || 'Something went wrong');
    }
    return response.json();
}