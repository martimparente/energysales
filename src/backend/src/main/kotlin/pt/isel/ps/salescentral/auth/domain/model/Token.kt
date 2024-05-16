package pt.isel.ps.ecoenergy.auth.domain.model

data class Token(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
)
