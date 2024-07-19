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
}

export interface UpdateClientInputModel {
    name: string;
    nif: string;
    phone: string;
    location: Location;
}

interface Location {
    district: string;
}