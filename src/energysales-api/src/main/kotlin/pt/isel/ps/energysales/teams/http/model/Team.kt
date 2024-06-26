package pt.isel.ps.energysales.teams.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.domain.model.Location
import pt.isel.ps.energysales.teams.domain.model.Team
import pt.isel.ps.energysales.teams.domain.model.TeamDetails

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
    val id: String,
    val name: String,
    val location: LocationJSON,
    val managerId: Int?,
) {
    companion object {
        fun fromTeam(team: Team) =
            TeamJSON(
                id = team.id.toString(),
                name = team.name,
                location = LocationJSON.fromLocation(team.location),
                managerId = team.managerId,
            )
    }
}

@Serializable
data class TeamDetailsJSON(
    val team: TeamJSON,
    val members: List<SellerJSON>,
) {
    companion object {
        fun fromTeamDetails(teamDetails: TeamDetails) =
            TeamDetailsJSON(
                team = TeamJSON.fromTeam(teamDetails.team),
                members = teamDetails.members.map { SellerJSON.fromSeller(it) },
            )
    }
}

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
data class AddTeamSellerRequest(
    val sellerId: String,
    val teamId: String,
)
