package pt.isel.ps.energysales.clients.domain

import pt.isel.ps.energysales.teams.domain.Location

data class Client(
    val id: String? = null,
    val name: String,
    val nif: String,
    val phone: String,
    val email: String,
    val location: Location,
    val sellerId: String?,
)
