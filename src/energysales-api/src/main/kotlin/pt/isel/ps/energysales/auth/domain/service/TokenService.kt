package pt.isel.ps.energysales.auth.domain.service

interface TokenService {
    fun generateJwtToken(
        username: String,
        roles: List<String>,
        expireInt: Int,
    ): String
}
