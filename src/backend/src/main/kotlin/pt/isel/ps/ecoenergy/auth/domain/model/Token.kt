package pt.isel.ps.ecoenergy.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
    // val scopre: String,
)
