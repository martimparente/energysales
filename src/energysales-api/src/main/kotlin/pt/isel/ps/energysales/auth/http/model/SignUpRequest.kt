package pt.isel.ps.energysales.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val repeatPassword: String,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
)
