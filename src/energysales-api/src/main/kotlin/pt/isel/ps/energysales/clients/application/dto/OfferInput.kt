package pt.isel.ps.energysales.clients.application.dto

data class CreateOfferInput(
    val clientId: String,
    val serviceId: String,
    val dueInDays: Int,
    val createdBy: String,
)

data class CreateOfferOutput(
    val url: String,
    val dueDate: String,
)
