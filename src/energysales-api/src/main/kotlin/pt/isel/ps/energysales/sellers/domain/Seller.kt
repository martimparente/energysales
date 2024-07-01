package pt.isel.ps.energysales.sellers.domain

import pt.isel.ps.energysales.auth.domain.model.User

data class Seller(
    val user: User,
    val totalSales: Float,
    val team: Int? = null,
)
