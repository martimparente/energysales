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
    id: string
    name: string;
    nif: string;
    phone: string;
    location: Location;
}

export interface MakeOfferInputModel {
    clientId: string;
    serviceId: string;
    dueInDays: number;
}

export interface PatchClientInputModel {
    id: string,
    name: string | null,
    nif: string | null,
    phone: string | null,
    location: Location | null,
}

interface Location {
    district: string;
}