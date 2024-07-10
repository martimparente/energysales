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
    username: string;
    password: string;
    repeatPassword: string;
    name: string;
    surname: string;
    email: string;
    role: string;
}

export interface UpdateUserInputModel {
    id: string,
    name: string,
    surname: string,
    email: string,
    team: string
}