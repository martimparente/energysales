package pt.isel.ps.energysales.teams.application.dto

import pt.isel.ps.energysales.teams.domain.Location

data class CreateTeamInput(
    val name: String,
    val location: Location,
    val managerId: String?,
)

data class UpdateTeamInput(
    val id: String,
    val name: String?,
    val location: Location?,
    val managerId: String?,
)
