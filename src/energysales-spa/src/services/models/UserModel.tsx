export interface User {
    id: string
    name: string
    surname: string
    email: string
    role: string
}

export interface UserProfile {
    username: string
    userId: string
    role: string
}

export interface Seller {
    id: string
    name: string
    surname: string
    email: string
    role: string
}

export interface ManagerInfo {
    id: string
    name: string
    surname: string
    email: string
    // todo avatar
}

export interface CreateUserInputModel {
    username: string
    password: string
    repeatPassword: string
    name: string
    surname: string
    email: string
    role: string
}

export interface PatchUserInputModel {
    id: string
    name: string | null
    surname: string | null
    email: string | null
    team: string | null
}
