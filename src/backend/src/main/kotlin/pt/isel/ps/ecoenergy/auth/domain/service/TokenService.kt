package pt.isel.ps.ecoenergy.auth.domain.service

import pt.isel.ps.ecoenergy.auth.domain.model.Token

interface TokenService {
    fun generateToken(uid: Int): Token
}
