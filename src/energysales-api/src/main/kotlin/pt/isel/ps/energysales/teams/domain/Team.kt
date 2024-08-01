package pt.isel.ps.energysales.teams.domain

import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.domain.Service

data class Team(
    val id: String? = null,
    val name: String,
    val location: Location,
    val managerId: String?,
    val avatarPath: String? = null,
)

data class TeamDetails(
    val team: Team,
    val members: List<Seller>,
    val services: List<Service>,
)
