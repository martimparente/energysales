package pt.isel.ps.energysales.users.domain

import java.util.Locale

enum class Role { ADMIN, SELLER, MANAGER, NONE }

fun String.toRole(): Role =
    when (this.uppercase(Locale.getDefault())) {
        "ADMIN" -> Role.ADMIN
        "SELLER" -> Role.SELLER
        "MANAGER" -> Role.MANAGER
        "NONE" -> Role.NONE
        else -> throw IllegalArgumentException("Invalid role")
    }
