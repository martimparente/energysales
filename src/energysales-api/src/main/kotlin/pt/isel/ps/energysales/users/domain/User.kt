package pt.isel.ps.energysales.users.domain

data class User(
    val id: String? = null,
    val name: String,
    val surname: String,
    val email: String,
    val role: Role,
)

data class UserCredentials(
    val id: String? = null,
    val username: String,
    val pwHash: String,
    val salt: String,
)

data class Manager(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
)
