package pt.isel.ps.energysales.users.application

interface TokenService {
    fun generateJwtToken(
        username: String,
        role: String,
        expireInt: Int,
    ): String
}
