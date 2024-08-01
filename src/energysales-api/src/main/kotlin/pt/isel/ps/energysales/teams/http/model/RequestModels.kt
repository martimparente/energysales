package pt.isel.ps.energysales.teams.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: Int?,
)

@Serializable
data class UpdateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val managerId: Int?,
)

@Serializable
data class AddTeamToSellerRequest(
    val teamId: String,
    val sellerId: String,
)

@Serializable
data class AddServiceToTeamRequest(
    val teamId: String,
    val serviceId: String,
)

@Serializable
data class AddClientToTeamRequest(
    val teamId: String,
    val clientId: String,
)
