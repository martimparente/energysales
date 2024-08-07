package pt.isel.ps.energysales.users.application.security

interface TokenService {
    fun generateJwtToken(
        username: String,
        userId: String,
        role: String,
        expiresIn: Int?,
    ): String
}
