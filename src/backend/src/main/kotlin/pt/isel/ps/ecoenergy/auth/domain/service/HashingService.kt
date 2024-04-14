package pt.isel.ps.ecoenergy.auth.domain.service

import pt.isel.ps.ecoenergy.auth.domain.model.SaltedHash

interface HashingService {
    fun generateSaltedHash(password: CharSequence, saltNumOfBytes: Int): SaltedHash

    fun matches(password: CharSequence, saltedHash: SaltedHash): Boolean
}
