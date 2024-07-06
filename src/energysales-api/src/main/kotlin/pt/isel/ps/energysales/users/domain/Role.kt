package pt.isel.ps.energysales.users.domain

enum class Role { ADMIN, SELLER, MANAGER, NONE }

fun String.toRole(): Role =
    when (this) {
        "ADMIN" -> Role.ADMIN
        "SELLER" -> Role.SELLER
        "MANAGER" -> Role.MANAGER
        "NONE" -> Role.NONE
        else -> throw IllegalArgumentException("Invalid role")
    }
