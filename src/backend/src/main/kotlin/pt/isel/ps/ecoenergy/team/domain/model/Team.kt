package pt.isel.ps.ecoenergy.team.domain.model

data class Team(
    val id: Int,
    val name: String,
    val location: String,
    val manager: Person?,
)
