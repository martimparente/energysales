package pt.isel.ps.energysales.clients.application.dto

import pt.isel.ps.energysales.teams.domain.Location

data class CreateClientInput(
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: Location,
    val sellerId: String,
)

data class UpdateClientInput(
    val id: String,
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: Location,
    val sellerId: String,
)
