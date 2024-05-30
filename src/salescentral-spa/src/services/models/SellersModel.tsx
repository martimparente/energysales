export interface Seller {
    person: Person;
    team: string;
    totalSales: string;
}

export interface Person {
    id: string;
    name: string;
    surname: string;
    email: string;
    team: string;
}

export interface CreateSellerInputModel {
    name: string,
    surname: string,
    email: string,
    team: string
}

export interface UpdateSellerInputModel {
    name: string,
    surname: string,
    email: string,
    team: string
}