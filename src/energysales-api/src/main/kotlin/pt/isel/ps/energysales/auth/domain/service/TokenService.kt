package pt.isel.ps.energysales.auth.domain.service

interface TokenService {
    fun generateJwtToken(
        username: String,
        role: String,
        expireInt: Int,
    ): String
}
