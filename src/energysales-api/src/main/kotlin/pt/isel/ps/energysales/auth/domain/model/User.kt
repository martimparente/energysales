package pt.isel.ps.energysales.auth.domain.model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
    val role: Set<Role>,
)
