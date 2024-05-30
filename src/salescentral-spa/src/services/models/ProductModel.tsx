export interface Product {
    id: string;
    name: string;
    price: string;
    description: string;
}

export interface CreateProductInputModel {
    name: string,
    price: number,
    description: string,
    image: string,
}

export interface UpdateProductInputModel {
    name: string,
    price: number,
    description: string,
    image: string,
}
