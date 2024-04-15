package pt.isel.ps.ecoenergy.auth.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.auth.domain.model.Token

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
) {
    companion object {
        fun fromToken(token: Token): LoginResponse {
            return LoginResponse(
                token = token.token,
                tokenType = token.tokenType,
                expiresIn = token.expiresIn
            )
        }
    }
}
