package pt.isel.ps.energysales.auth.domain.service

import pt.isel.ps.energysales.auth.domain.model.Token

interface TokenService {
    fun generateToken(uid: Int): Token
}
