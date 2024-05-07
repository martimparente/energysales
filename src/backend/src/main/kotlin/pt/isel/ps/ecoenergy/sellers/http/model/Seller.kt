package pt.isel.ps.ecoenergy.sellers.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.sellers.domain.model.Seller

@Serializable
data class SellerJSON(
    val person: PersonJSON,
    val totalSales: Float,
    val team: Int?,
) {
    companion object {
        fun fromSeller(seller: Seller) =
            SellerJSON(
                PersonJSON.fromPerson(seller.person),
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
    val name: String,
    val surname: String,
    val email: String,
    val totalSales: Float,
    val team: Int? = null,
)
