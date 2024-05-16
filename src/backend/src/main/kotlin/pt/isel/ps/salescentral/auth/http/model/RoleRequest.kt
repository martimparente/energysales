package pt.isel.ps.ecoenergy.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val role: String,
)
