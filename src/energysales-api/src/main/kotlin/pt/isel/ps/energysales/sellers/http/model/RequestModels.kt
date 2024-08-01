package pt.isel.ps.energysales.sellers.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSellerRequest(
    val name: String,
    val surname: String,
    val email: String,
    val team: String? = null,
)

@Serializable
data class UpdateSellerRequest(
    val uid: String,
    val totalSales: Float,
    val team: String? = null,
)
