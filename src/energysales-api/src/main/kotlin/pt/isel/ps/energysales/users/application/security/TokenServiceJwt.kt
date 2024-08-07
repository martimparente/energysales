package pt.isel.ps.energysales.users.application.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expiresIn: Int,
)

class TokenServiceJwt(
    private val config: JwtConfig,
) : TokenService {
    override fun generateJwtToken(
        username: String,
        userId: String,
        role: String,
        expiresIn: Int?,
    ): String =
        JWT
            .create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("username", username)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + (expiresIn ?: config.expiresIn)))
            .sign(Algorithm.HMAC256(config.secret))
}
