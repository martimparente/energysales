import {Seller} from "./SellersModel.tsx";

export interface Team {
    id: string;
    name: string;
    manager: string;
    location: Location;
}

interface Location {
    district: string;
}

export interface TeamDetails {
    team: Team;
    members: Seller[];
}

export interface CreateTeamInputModel {
    name: string,
    manager: string,
    location: string
}

export interface UpdateTeamInputModel {
    id: string,
    name: string,
    manager: string,
    location: string
}

export interface AddTeamSellerInputModel {
    teamId: string,
    sellerId: string
}
