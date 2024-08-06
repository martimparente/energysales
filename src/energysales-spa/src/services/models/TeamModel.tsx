import {User} from './UserModel.tsx'
import {Service} from './ServiceModel.tsx'

export interface Partner {
    id: string
    name: string
    manager: string
    location: Location
    avatarPath: string | null
}

interface Location {
    district: string
}

export interface PartnerDetails {
    partner: Partner
    members: User[]
    services: Service[]
}

export interface CreatePartnerInputModel {
    name: string
    managerId: string
    location: Location
}

export interface UpdatePartnerInputModel {
    name: string
    managerId: string
    location: Location
}

export interface PatchPartnerInputModel {
    name: string | null
    managerId: string | null
    location: Location | null
}

export interface AddPartnerSellerInputModel {
    sellerId: string
}

export interface DeletePartnerSellerParams {
    partnerId: string
    sellerId: string
}

export interface AddPartnerServiceInputModel {
    serviceId: string
}

export interface DeletePartnerServiceParams {
    partnerId: string
    serviceId: string
}

export interface AddPartnerAvatarInputModel {
    avatarImg: File
}
