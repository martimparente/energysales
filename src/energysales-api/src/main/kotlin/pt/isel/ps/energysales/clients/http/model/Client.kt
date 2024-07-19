package pt.isel.ps.energysales.clients.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.domain.Location

@Serializable
data class ClientJSON(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val location: LocationJSON,
) {
    companion object {
        fun fromClient(client: Client) =
            ClientJSON(
                client.id,
                client.name,
                client.nif,
                client.phone,
                LocationJSON.fromLocation(client.location),
            )
    }
}

@Serializable
data class LocationJSON(
    val district: String,
) {
    fun toLocation() = Location(this.district)

    companion object {
        fun fromLocation(location: Location) =
            LocationJSON(
                district = location.district,
            )
    }
}

@Serializable
data class CreateClientRequest(
    val name: String,
    val nif: String,
    val phone: String,
    val location: LocationJSON,
    val sellerId: String,
)

@Serializable
data class UpdateClientRequest(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val location: LocationJSON,
    val sellerId: String,
)
