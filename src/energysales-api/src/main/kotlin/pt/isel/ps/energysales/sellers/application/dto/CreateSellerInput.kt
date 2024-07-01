package pt.isel.ps.energysales.sellers.application.dto

data class CreateSellerInput(
    val name: String,
    val surname: String,
    val email: String,
    val team: Int? = null,
)
