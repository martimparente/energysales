package pt.isel.ps.energysales.clients.application.dto

import pt.isel.ps.energysales.teams.domain.Location

data class CreateClientInput(
    val name: String,
    val nif: String,
    val phone: String,
    val location: Location,
    val sellerId: String,
)

data class UpdateClientInput(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val location: Location,
    val sellerId: String,
)
