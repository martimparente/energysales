package pt.isel.ps.energysales.teams.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
)

@Serializable
data class UpdateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
)

@Serializable
data class PatchTeamRequest(
    val name: String? = null,
    val location: LocationJSON? = null,
    val managerId: String? = null,
)

@Serializable
data class AddTeamSellerRequest(
    val sellerId: String,
)

@Serializable
data class AddTeamServiceRequest(
    val serviceId: String,
)

@Serializable
data class AddTeamClientRequest(
    val clientId: String,
)
