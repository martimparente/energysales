package pt.isel.ps.ecoenergy.teams.domain.model

data class Team(
    val id: Int,
    val name: String,
    val location: Location,
    val manager: Person?,
)