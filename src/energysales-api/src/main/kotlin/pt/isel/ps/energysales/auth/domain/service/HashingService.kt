package pt.isel.ps.energysales.auth.domain.service

import pt.isel.ps.energysales.auth.domain.model.SaltedHash

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
