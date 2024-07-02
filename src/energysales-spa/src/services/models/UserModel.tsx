export interface User {
    id: string;
    name: string;
    surname: string;
    email: string;
    role: string;
}

export interface ManagerInfo {
    id: string;
    name: string;
    surname: string;
    email: string;
    // todo avatar
}

export interface CreateUserInputModel {
    name: string,
    surname: string,
    email: string,
    team: string
}

export interface UpdateUserInputModel {
    name: string,
    surname: string,
    email: string,
    team: string
}