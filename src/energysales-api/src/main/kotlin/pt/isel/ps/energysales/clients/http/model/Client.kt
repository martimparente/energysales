package pt.isel.ps.energysales.clients.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.domain.District
import pt.isel.ps.energysales.teams.domain.Location

@Serializable
data class ClientJSON(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: LocationJSON,
) {
    companion object {
        fun fromClient(client: Client) =
            ClientJSON(
                client.id,
                client.name,
                client.nif,
                client.phone,
                client.email,
                LocationJSON.fromLocation(client.location),
            )
    }
}

@Serializable
data class LocationJSON(
    val district: String,
) {
    fun toLocation() = Location(District.fromName(district))

    companion object {
        fun fromLocation(location: Location) =
            LocationJSON(
                district = location.district.districtName,
            )
    }
}

@Serializable
data class OfferLinkJSON(
    val url: String,
    val dueDate: String,
)

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
    val id: Int,
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
