package pt.isel.ps.ecoenergy.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val repeatPassword: String,
)
