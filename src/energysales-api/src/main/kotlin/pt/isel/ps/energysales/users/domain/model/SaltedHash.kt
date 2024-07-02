package pt.isel.ps.energysales.users.domain.model

data class SaltedHash(
    val hash: String,
    val salt: String,
)
