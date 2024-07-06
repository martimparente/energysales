package pt.isel.ps.energysales.users.domain

data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val role: Role,
)

data class UserCredentials(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
)

data class Manager(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
)
