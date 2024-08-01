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

export interface AddTeamSellerInputModel {
    teamId: string
    sellerId: string
}

export interface DeleteTeamSellerInput {
    teamId: string
    sellerId: string
}

export interface AddTeamServiceInputModel {
    teamId: string
    serviceId: string
}

export interface DeleteTeamServiceInput {
    teamId: string
    serviceId: string
}

export interface AddTeamAvatarInputModel {
    teamId: string
    avatarImg: File
}
