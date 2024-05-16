package pt.isel.ps.salescentral.products.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.salescentral.products.domain.model.Product

@Serializable
data class ProductJSON(
    val int: Int,
    val name: String,
) {
    companion object {
        fun fromProduct(product: Product) =
            ProductJSON(
                product.id,
                product.name,
            )
    }
}

@Serializable
data class CreateProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
)

@Serializable
data class UpdateProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
)
