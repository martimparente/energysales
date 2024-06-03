package pt.isel.ps.energysales.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val role: String,
)
