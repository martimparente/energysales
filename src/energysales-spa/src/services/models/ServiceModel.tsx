export interface Service {
    id: string
    name: string
    description: string
    cycleName: string
    cycleType: string
    periodName: string
    periodNumPeriods: number
    price: Price
}

export interface Price {
    ponta: number
    cheia: number | null
    vazio: number | null
    superVazio: number | null
    operadorMercado: number
    gdo: number
    omip: number | null
    margem: number
}

export interface CreateServiceInputModel {
    name: string
    description: string
    cycleName: string
    cycleType: string
    periodName: string
    periodNumPeriods: number
    price: Price
}

export interface UpdateServiceInputModel {
    id: string
    name: string
    description: string
    cycleName: string
    cycleType: string
    periodName: string
    periodNumPeriods: number
    price: Price
}
