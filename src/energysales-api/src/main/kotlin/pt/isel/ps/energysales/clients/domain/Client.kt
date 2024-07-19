package pt.isel.ps.energysales.clients.domain

import pt.isel.ps.energysales.teams.domain.Location

data class Client(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val location: Location,
    val sellerId: Int?,
)
