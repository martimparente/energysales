package pt.isel.ps.energysales.teams.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.services.http.model.ServiceJSON
import pt.isel.ps.energysales.teams.domain.Location
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails

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

    fun toLocation() = Location(district)
}

@Serializable
data class TeamJSON(
    val id: String,
    val name: String,
    val location: LocationJSON,
    val managerId: String?,
    val avatarPath: String?,
) {
    companion object {
        fun fromTeam(team: Team) =
            TeamJSON(
                id = team.id!!,
                name = team.name,
                location = LocationJSON.fromLocation(team.location),
                managerId = team.managerId,
                avatarPath = team.avatarPath,
            )
    }
}

@Serializable
data class TeamDetailsJSON(
    val team: TeamJSON,
    val members: List<SellerJSON>,
    val services: List<ServiceJSON>,
) {
    companion object {
        fun fromTeamDetails(teamDetails: TeamDetails) =
            TeamDetailsJSON(
                team = TeamJSON.fromTeam(teamDetails.team),
                members = teamDetails.members.map { SellerJSON.fromSeller(it) },
                services = teamDetails.services.map { ServiceJSON.fromService(it) },
            )
    }
}
