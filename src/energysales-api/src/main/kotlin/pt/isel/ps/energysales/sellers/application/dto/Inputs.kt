package pt.isel.ps.energysales.sellers.application.dto

data class CreateSellerInput(
    val name: String,
    val surname: String,
    val email: String,
    val team: String? = null,
)

data class GetAllSellerInput(
    val lastKeySeen: String? = null,
    val noTeam: Boolean = false,
    val searchQuery: String?,
)

data class UpdateSellerInput(
    val id: String,
    val team: String,
)
