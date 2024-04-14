package pt.isel.ps.ecoenergy.auth.security

import pt.isel.ps.ecoenergy.auth.domain.model.SaltedHash
import pt.isel.ps.ecoenergy.auth.domain.service.HashingService
import java.security.MessageDigest
import java.security.SecureRandom

@OptIn(ExperimentalStdlibApi::class) // For toHexString
class SHA256HashingService : HashingService {
    /**
     * Generates a salted hash for the given password.
     *
     * @param password The password to hash.
     * @param saltNumOfBytes The number of bytes to use for the salt.
     * @return The SaltedHash
     */
    override fun generateSaltedHash(
        password: CharSequence,
        saltNumOfBytes: Int,
    ): SaltedHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltNumOfBytes)
        val saltHex = salt.toHexString()
        val hash = MessageDigest.getInstance("SHA-256").digest(salt).toHexString()

        return SaltedHash(
            hash = hash,
            salt = saltHex,
        )
    }

    /**
     * Matches the given password with the given salted hash.
     *
     * @param password The password to match.
     * @param saltedHash The salted hash to match against.
     * @return True if the password matches the salted hash
     */
    override fun matches(
        password: CharSequence,
        saltedHash: SaltedHash,
    ): Boolean = MessageDigest.getInstance("SHA-256").digest(saltedHash.salt.toByteArray()).toHexString() == saltedHash.hash
}
