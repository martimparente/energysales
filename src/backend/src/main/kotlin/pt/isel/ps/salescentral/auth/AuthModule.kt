package pt.isel.ps.ecoenergy.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import pt.isel.ps.ecoenergy.auth.domain.service.security.JwtConfig

data class AuthenticationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Throwable(message, cause)

fun Application.configureAuth(jwtConfig: JwtConfig) {
    authentication {
        jwt {
            realm = jwtConfig.realm

            // This is the function that will be called when no token is provided
            challenge { _, _ ->
                call.request.headers["Authorization"]?.let {
                    throw AuthenticationException("Token is invalid")
                } ?: throw AuthenticationException("No Authorization Header found")
            }
            // This is the verifier that will check if the token is valid
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build(),
            )
            // This is the function that will be called when the token is valid
            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    throw AuthenticationException("Token is invalid")
                }
            }
        }
    }
}
