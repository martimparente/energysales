package pt.isel.ps.energysales.sellers.domain.model

data class Seller(
    val uid: Int,
    val totalSales: Float,
    val team: Int? = null,
)
