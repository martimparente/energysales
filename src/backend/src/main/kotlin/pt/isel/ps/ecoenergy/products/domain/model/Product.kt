package pt.isel.ps.ecoenergy.products.domain.model

data class Product(
    val id: Int,
    val name: String,
) {
    fun createProduct(): Product = Product(id, name)
}
