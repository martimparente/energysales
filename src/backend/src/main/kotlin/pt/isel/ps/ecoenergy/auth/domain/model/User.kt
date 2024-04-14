package pt.isel.ps.ecoenergy.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
)
