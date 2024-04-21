package pt.isel.ps.ecoenergy.team.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.team.domain.model.Person
import pt.isel.ps.ecoenergy.team.domain.model.Team


@Serializable
data class TeamJson(
    val id: Int,
    val name: String,
    val location: String,
    val manager: Int?
) {
    companion object {
        fun toTeam(teamJson: TeamJson) = Team(
            id = teamJson.id,
            name = teamJson.name,
            location = teamJson.location,
            manager = teamJson.manager?.let { Person.create(it) }
        )

        fun fromTeam(team: Team) = TeamJson(
            id = team.id,
            name = team.name,
            location = team.location,
            manager = team.manager?.uid
        )
    }
}

@Serializable
data class CreateTeamRequest(
    val name: String,
    val location: String,
    val manager: Int?
)

@Serializable
data class UpdateTeamRequest(
    val name: String,
    val location: String,
    val manager: Int?
)
