package pt.isel.ps.ecoenergy.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class JwtTokenServiceTest {
    @Test
    fun `generate JWT Token and validates`() {
        testApplication {
            environment {
                config = MapApplicationConfig("ktor.environment" to "test")
            }
            // Arrange
            val jwtConfig = JWTConfig("secret", "issuer", "audience", "realm")
            val jwtTokenService = JwtTokenService(jwtConfig)

            // Act
            val token = jwtTokenService.generateToken(0)
            print(token.token)
            // Assert
            install(Authentication) {
                jwt {
                    realm = "realm"
                    verifier(
                        JWT
                            .require(Algorithm.HMAC256(jwtConfig.secret))
                            .withAudience(jwtConfig.audience)
                            .withIssuer(jwtConfig.issuer)
                            .build(),
                    )
                }
            }
        }
    }
}
