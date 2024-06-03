package pt.isel.ps.energysales.products.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val image: String?,
)
