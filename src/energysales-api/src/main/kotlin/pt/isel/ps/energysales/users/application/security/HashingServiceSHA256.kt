package pt.isel.ps.energysales.users.application.security

import pt.isel.ps.energysales.users.domain.SaltedHash
import java.security.MessageDigest
import java.security.SecureRandom

@OptIn(ExperimentalStdlibApi::class) // For toHexString
class HashingServiceSHA256 : HashingService {
    /**
     * Generates a salted hash for the given password
     *
     * @param password The password to hash.
     * @param saltNumOfBytes The number of bytes to use for the salt.
     * @return The SaltedHash
     */
    override fun generateSaltedHash(
        password: CharSequence,
        saltNumOfBytes: Int,
    ): SaltedHash {
        val saltHex = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltNumOfBytes).toHexString()
        val saltAndPassword = "$saltHex$password"
        val hash = MessageDigest.getInstance("SHA-256").digest(saltAndPassword.toByteArray()).toHexString()
        return SaltedHash(hash = hash, salt = saltHex)
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
    ): Boolean {
        val saltAndPassword = "${saltedHash.salt}$password"
        val hash = MessageDigest.getInstance("SHA-256").digest(saltAndPassword.toByteArray()).toHexString()
        return hash == saltedHash.hash
    }
}
