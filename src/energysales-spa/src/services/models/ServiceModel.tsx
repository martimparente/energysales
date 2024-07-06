export interface Service {
    id: string;
    name: string;
    description: string;
    cycleName: string;
    cycleType: string;
    periodName: string;
    periodNumPeriods: number;
    price: Price;
}

export interface Price {
    ponta: number;
    cheia: number;
    vazio: number;
    superVazio: number;
    operadorMercado: number;
    gdo: number;
    omip: number;
    margem: number;
}

export interface CreateServiceInputModel {
    name: string;
    description: string;
    cycleName: string;
    cycleType: string;
    periodName: string;
    periodNumPeriods: number;
    price: Price;
}

export interface UpdateServiceInputModel {
    id: string;
    name: string;
    description: string;
    cycleName: string;
    cycleType: string;
    periodName: string;
    periodNumPeriods: number;
    price: Price;
}
