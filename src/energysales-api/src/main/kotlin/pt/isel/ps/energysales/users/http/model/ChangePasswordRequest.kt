package pt.isel.ps.energysales.users.http.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val repeatNewPassword: String,
)
