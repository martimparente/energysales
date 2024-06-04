package pt.isel.ps.energysales.auth.domain.service

interface TokenService {
    fun generateJwtToken(
        uid: Int,
        roles: List<String>,
        expireInt: Int,
    ): String
}
