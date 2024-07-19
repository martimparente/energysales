package pt.isel.ps.energysales.users.application

interface TokenService {
    fun generateJwtToken(
        username: String,
        userId: String,
        role: String,
        expireInt: Int,
    ): String
}
