package pt.isel.ps.ecoenergy.auth.domain.model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
)
