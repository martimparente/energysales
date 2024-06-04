package pt.isel.ps.energysales.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val repeatPassword: String,
    val roles: Set<String>,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
)
