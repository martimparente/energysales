package pt.isel.ps.energysales.teams.application.dto

data class CreateTeamInput(
    val name: String,
    val district: String,
    val managerId: Int?,
)

data class UpdateTeamInput(
    val id: Int,
    val name: String?,
    val district: String?,
    val managerId: Int?,
)
