package pt.isel.ps.energysales.partners.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.partners.domain.District
import pt.isel.ps.energysales.partners.domain.Location
import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.partners.domain.PartnerDetails
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.services.http.model.ServiceJSON

@Serializable
data class LocationJSON(
    val district: String,
) {
    companion object {
        fun fromLocation(location: Location) =
            LocationJSON(
                district = location.district.districtName,
            )
    }

    fun toLocation() = Location(District.fromName(district))
}

@Serializable
data class PartnerJSON(
    val id: String,
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
    val avatarPath: String?,
) {
    companion object {
        fun fromPartner(partner: Partner) =
            PartnerJSON(
                id = partner.id!!,
                name = partner.name,
                location = LocationJSON.fromLocation(partner.location),
                managerId = partner.managerId,
                avatarPath = partner.avatarPath,
            )
    }
}

@Serializable
data class PartnerDetailsJSON(
    val partner: PartnerJSON,
    val members: List<SellerJSON>,
    val services: List<ServiceJSON>,
) {
    companion object {
        fun fromPartnerDetails(partnerDetails: PartnerDetails) =
            PartnerDetailsJSON(
                partner = PartnerJSON.fromPartner(partnerDetails.partner),
                members = partnerDetails.members.map { SellerJSON.fromSeller(it) },
                services = partnerDetails.services.map { ServiceJSON.fromService(it) },
            )
    }
}

@Serializable
data class AvatarJSON(
    val avatar: String,
)
