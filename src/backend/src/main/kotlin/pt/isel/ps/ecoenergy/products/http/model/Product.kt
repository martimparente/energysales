package pt.isel.ps.ecoenergy.products.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.products.domain.model.Product

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
)

@Serializable
data class UpdateProductRequest(
    val name: String,
)
