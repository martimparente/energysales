package pt.isel.ps.salescentral.auth.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.salescentral.auth.domain.model.Token

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
) {
    companion object {
        fun fromToken(token: Token) =
            LoginResponse(
                token = token.token,
                tokenType = token.tokenType,
                expiresIn = token.expiresIn,
            )
    }
}
