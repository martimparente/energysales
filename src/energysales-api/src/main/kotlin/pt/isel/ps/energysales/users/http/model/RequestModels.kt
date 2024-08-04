package pt.isel.ps.energysales.users.http.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val repeatNewPassword: String,
)

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val repeatPassword: String,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
)

@Serializable
data class PatchUserRequest(
    val id: String,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val role: String? = null,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
)

@Serializable
data class RoleRequest(
    val role: String,
)
