import {User} from './UserModel.tsx'
import {Service} from './ServiceModel.tsx'

export interface Team {
    id: string
    name: string
    manager: string
    location: Location
    avatarPath: string | null
}

interface Location {
    district: string
}

export interface TeamDetails {
    team: Team
    members: User[]
    services: Service[]
}

export interface CreateTeamInputModel {
    name: string
    managerId: string
    location: Location
}

export interface UpdateTeamInputModel {
    name: string
    managerId: string
    location: Location
}

export interface PatchTeamInputModel {
    name: string | null
    managerId: string | null
    location: Location | null
}

export interface AddTeamSellerInputModel {
    sellerId: string
}

export interface DeleteTeamSellerParams {
    teamId: string
    sellerId: string
}

export interface AddTeamServiceInputModel {
    serviceId: string
}

export interface DeleteTeamServiceParams {
    teamId: string
    serviceId: string
}

export interface AddTeamAvatarInputModel {
    avatarImg: File
}
