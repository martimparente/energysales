package pt.isel.ps.ecoenergy.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val repeatNewPassword: String,
)
