package pt.isel.ps.salescentral.teams.http.model

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val uid: Int,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
)
