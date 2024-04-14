package pt.isel.ps.ecoenergy.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)
