package pt.isel.ps.ecoenergy.sellers.domain.model

data class Seller(
    val person: Person,
    val totalSales: Float,
    val team: Int?,
)
