package pt.isel.ps.energysales.sellers.domain

import pt.isel.ps.energysales.users.domain.User

data class Seller(
    val user: User,
    val totalSales: Float,
    val team: String? = null,
)
