package pt.isel.ps.ecoenergy.auth.domain.model

data class SaltedHash(
    val hash: String,
    val salt: String,
)
