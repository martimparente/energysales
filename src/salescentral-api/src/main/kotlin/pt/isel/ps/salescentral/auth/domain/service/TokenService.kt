package pt.isel.ps.salescentral.auth.domain.service

import pt.isel.ps.salescentral.auth.domain.model.Token

interface TokenService {
    fun generateToken(uid: Int): Token
}
