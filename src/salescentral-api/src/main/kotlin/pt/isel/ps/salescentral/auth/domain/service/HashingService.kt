package pt.isel.ps.salescentral.auth.domain.service

import pt.isel.ps.salescentral.auth.domain.model.SaltedHash

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
