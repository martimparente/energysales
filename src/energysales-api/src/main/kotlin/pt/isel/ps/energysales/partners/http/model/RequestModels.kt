package pt.isel.ps.energysales.partners.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePartnerRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
)

@Serializable
data class UpdatePartnerRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
)

@Serializable
data class PatchPartnerRequest(
    val name: String? = null,
    val location: LocationJSON? = null,
    val managerId: String? = null,
)

@Serializable
data class AddPartnerSellerRequest(
    val sellerId: String,
)

@Serializable
data class AddPartnerServiceRequest(
    val serviceId: String,
)

@Serializable
data class AddPartnerClientRequest(
    val clientId: String,
)
