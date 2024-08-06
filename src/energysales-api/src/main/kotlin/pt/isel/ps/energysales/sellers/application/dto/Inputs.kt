package pt.isel.ps.energysales.sellers.application.dto

data class CreateSellerInput(
    val name: String,
    val surname: String,
    val email: String,
    val partner: String? = null,
)

data class GetAllSellerInput(
    val lastKeySeen: String? = null,
    val noPartner: Boolean = false,
    val searchQuery: String?,
)

data class UpdateSellerInput(
    val id: String,
    val partner: String,
)
