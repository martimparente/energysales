package pt.isel.ps.salescentral.auth.domain.model

data class Token(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
)
