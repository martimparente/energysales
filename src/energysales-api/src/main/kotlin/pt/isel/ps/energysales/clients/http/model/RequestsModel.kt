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
data class UpdateClientRequest(
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: LocationJSON,
)

@Serializable
data class CreateOfferRequest(
    val clientId: String,
    val serviceId: String,
    val dueInDays: Int,
)
