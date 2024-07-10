package pt.isel.ps.energysales.clients.application.dto

data class CreateClientInput(
    val name: String,
    val nif: String,
    val phone: String,
    val district: String,
    val teamId: Int,
    val sellerId: Int?,
)

data class UpdateClientInput(
    val id: Int,
    val name: String,
    val nif: String,
    val phone: String,
    val district: String,
    val teamId: Int,
    val sellerId: Int?,
)
