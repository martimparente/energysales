package pt.isel.ps.energysales.auth.domain.service.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import pt.isel.ps.energysales.auth.domain.model.Token
import pt.isel.ps.energysales.auth.domain.service.TokenService
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
    override fun generateToken(uid: Int): Token {
        val token =
            JWT
                .create()
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .withClaim("uid", uid)
                .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
                .sign(Algorithm.HMAC256(config.secret))

        return Token(
            token = token,
            tokenType = "Bearer",
            expiresIn = 36000000000,
        )
    }
}
