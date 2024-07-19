export interface Client {
    id: string;
    name: string;
    nif: string;
    phone: string;
    location: Location;
}

export interface CreateClientInputModel {
    name: string;
    nif: string;
    phone: string;
    location: Location;
    sellerId: string;
}

export interface UpdateClientInputModel {
    name: string;
    nif: string;
    phone: string;
    location: Location;
    sellerId: string;
}

interface Location {
    district: string;
}