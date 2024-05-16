package pt.isel.ps.salescentral.auth.http.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val role: String,
)
