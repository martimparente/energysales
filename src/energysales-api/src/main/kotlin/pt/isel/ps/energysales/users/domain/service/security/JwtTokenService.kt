package pt.isel.ps.energysales.users.domain.service.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import pt.isel.ps.energysales.users.domain.service.TokenService
import java.util.Date

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
)

class JwtTokenService(
    private val config: JwtConfig,
) : TokenService {
    override fun generateJwtToken(
        username: String,
        role: String,
        expireInt: Int,
    ): String =
        JWT
            .create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + expireInt))
            .sign(Algorithm.HMAC256(config.secret))
}
