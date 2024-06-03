package pt.isel.ps.energysales.auth.domain.model

data class SaltedHash(
    val hash: String,
    val salt: String,
)
