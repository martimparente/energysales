package pt.isel.ps.ecoenergy.teams.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.teams.domain.model.Location
import pt.isel.ps.ecoenergy.teams.domain.model.Team

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
data class TeamJSON(
    val id: Int,
    val name: String,
    val location: LocationJSON,
    val manager: Int?,
) {
    companion object {
        fun fromTeam(team: Team) =
            TeamJSON(
                id = team.id,
                name = team.name,
                location = LocationJSON.fromLocation(team.location),
                manager = team.manager?.uid,
            )
    }
}

@Serializable
data class CreateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val manager: Int?,
)

@Serializable
data class UpdateTeamRequest(
    val name: String,
    val location: LocationJSON,
    val manager: Int?,
)
