package pt.isel.ps.ecoenergy.products.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
)
