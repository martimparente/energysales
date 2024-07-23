package pt.isel.ps.energysales.users.application.dto

data class CreateUserInput(
    val username: String,
    val password: String,
    val repeatPassword: String,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
)

data class PatchUserInput(
    val name: String?,
    val surname: String?,
    val email: String?,
    val role: String?,
)
