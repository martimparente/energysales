package pt.isel.ps.energysales.users.domain

data class SaltedHash(
    val hash: String,
    val salt: String,
)
