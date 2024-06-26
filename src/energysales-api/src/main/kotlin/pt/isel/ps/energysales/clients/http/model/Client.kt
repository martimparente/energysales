package pt.isel.ps.energysales.clients.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.clients.domain.model.Client
import pt.isel.ps.energysales.teams.domain.model.Location

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
    val district: String,
)

@Serializable
data class UpdateClientRequest(
    val name: String,
    val nif: String,
    val phone: String,
    val district: String,
)
