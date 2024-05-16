package pt.isel.ps.salescentral.auth.domain.model

data class SaltedHash(
    val hash: String,
    val salt: String,
)
