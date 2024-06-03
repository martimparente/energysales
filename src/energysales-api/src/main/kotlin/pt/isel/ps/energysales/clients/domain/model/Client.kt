package pt.isel.ps.energysales.clients.domain.model

import pt.isel.ps.energysales.teams.domain.model.Location

data class Client(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val location: Location,
)
