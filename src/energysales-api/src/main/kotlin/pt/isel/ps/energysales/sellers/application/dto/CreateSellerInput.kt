package pt.isel.ps.energysales.sellers.application.dto

data class CreateSellerInput(
    val name: String,
    val surname: String,
    val email: String,
    val team: Int? = null,
)

data class GetAllSellerInput(
    val lastKeySeen: Int? = null,
    val noTeam: Boolean = false,
    val searchQuery: String?,
)