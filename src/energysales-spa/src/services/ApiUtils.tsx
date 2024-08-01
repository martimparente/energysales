import {toast} from 'react-toastify'

const getAuthorizationHeader = () => ({
    'Content-Type': 'application/json',
    Authorization: `Bearer ${localStorage.getItem('token')}`
})

export async function fetchData<T>(url: string): Promise<T> {
    const response = await fetch(url, {headers: getAuthorizationHeader()})
    const data = await response.json()
    if (!response.ok) {
        toast.warning(data.title || 'Something went wrong')
        throw new Error(data.title)
    }
    return data
}

export async function mutateData<T>(url: string, method: string, body?: any): Promise<T | undefined> {
    const response = await fetch(url, {
        method,
        headers: getAuthorizationHeader(),
        body: JSON.stringify(body)
    })

    if (!response.ok) {
        const problem = await response.json()
        toast.warning(problem.title || 'Something went wrong')
    }
    // only get json if content type is json
    if (response.headers.get('Content-Type')?.includes('application/json')) return await response.json()
}

export async function mutateData1<T>(url: string, method: string, formData: FormData): Promise<T | undefined> {
    const response = await fetch(url, {
        method,
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
    })

    if (!response.ok) {
        const problem = await response.json()
        toast.warning(problem.title || 'Something went wrong')
    }
    // only get json if content type is json
    if (response.headers.get('Content-Type')?.includes('application/json')) return await response.json()
}
