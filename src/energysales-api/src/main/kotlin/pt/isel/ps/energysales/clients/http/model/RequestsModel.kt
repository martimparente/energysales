package pt.isel.ps.energysales.clients.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateClientRequest(
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: LocationJSON,
)

@Serializable
data class PatchClientRequest(
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val location: LocationJSON? = null,
    val sellerId: String? = null,
)

@Serializable
data class MakeOfferRequest(
    val clientId: String,
    val serviceId: String,
    val dueInDays: Int,
)
