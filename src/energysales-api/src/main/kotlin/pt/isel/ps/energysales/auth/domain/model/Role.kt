package pt.isel.ps.energysales.auth.domain.model

enum class Role { ADMIN, SELLER }

fun toRole(role: String): Role =
    when (role) {
        "ADMIN" -> Role.ADMIN
        "SELLER" -> Role.SELLER
        else -> throw IllegalArgumentException("Invalid role")
    }
