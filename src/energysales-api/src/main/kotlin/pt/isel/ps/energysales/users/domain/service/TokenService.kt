package pt.isel.ps.energysales.users.domain.service

interface TokenService {
    fun generateJwtToken(
        username: String,
        role: String,
        expireInt: Int,
    ): String
}
