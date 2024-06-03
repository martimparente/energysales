package pt.isel.ps.energysales.teams.domain.model

import pt.isel.ps.energysales.sellers.domain.model.Seller

data class Team(
    val id: Int,
    val name: String,
    val location: Location,
    val manager: Person?,
)

data class TeamDetails(
    val team: Team,
    val members: List<Seller>,
)
