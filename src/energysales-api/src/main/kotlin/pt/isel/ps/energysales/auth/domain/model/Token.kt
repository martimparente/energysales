package pt.isel.ps.energysales.auth.domain.model

data class Token(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
)
