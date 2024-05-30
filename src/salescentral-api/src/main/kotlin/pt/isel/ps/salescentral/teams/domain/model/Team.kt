package pt.isel.ps.salescentral.teams.domain.model

import pt.isel.ps.salescentral.sellers.domain.model.Seller

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
