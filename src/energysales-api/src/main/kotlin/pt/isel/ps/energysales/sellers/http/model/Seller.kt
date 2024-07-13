package pt.isel.ps.energysales.sellers.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.sellers.domain.Seller

@Serializable
data class SellerJSON(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val totalSales: Float,
    val team: Int?,
) {
    companion object {
        fun fromSeller(seller: Seller) =
            SellerJSON(
                seller.user.id.toString(),
                seller.user.name,
                seller.user.surname,
                seller.user.email,
                seller.totalSales,
                seller.team,
            )
    }
}

@Serializable
data class CreateSellerRequest(
    val name: String,
    val surname: String,
    val email: String,
    val team: Int? = null,
)

@Serializable
data class UpdateSellerRequest(
    val uid: String,
    val totalSales: Float,
    val team: Int? = null,
)
