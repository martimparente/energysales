package pt.isel.ps.energysales.auth.domain.model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
    val name: String,
    val surname: String,
    val email: String,
    val roles: Set<Role>,
)
