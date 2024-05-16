package pt.isel.ps.salescentral.auth.domain.model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
)
