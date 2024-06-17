package pt.isel.ps.energysales.sellers.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.sellers.domain.model.Seller

@Serializable
data class SellerJSON(
    val uid: String,
    val totalSales: Float,
    val team: Int?,
) {
    companion object {
        fun fromSeller(seller: Seller) =
            SellerJSON(
                seller.uid.toString(),
                seller.totalSales,
                seller.team,
            )
    }
}

@Serializable
data class CreateSellerRequest(
    val uid: String,
)

@Serializable
data class UpdateSellerRequest(
    val uid: String,
    val totalSales: Float,
    val team: Int? = null,
)
