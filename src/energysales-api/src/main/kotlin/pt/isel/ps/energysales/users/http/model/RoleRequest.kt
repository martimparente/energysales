package pt.isel.ps.energysales.users.http.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val role: String,
)
