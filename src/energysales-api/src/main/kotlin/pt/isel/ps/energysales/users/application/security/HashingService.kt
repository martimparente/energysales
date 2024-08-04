package pt.isel.ps.energysales.users.application.security

import pt.isel.ps.energysales.users.domain.SaltedHash

interface HashingService {
    fun generateSaltedHash(
        password: CharSequence,
        saltNumOfBytes: Int,
    ): SaltedHash

    fun matches(
        password: CharSequence,
        saltedHash: SaltedHash,
    ): Boolean
}
