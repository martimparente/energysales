package pt.isel.ps.ecoenergy.auth.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.ecoenergy.auth.domain.model.SaltedHash
import pt.isel.ps.ecoenergy.auth.domain.model.Token
import pt.isel.ps.ecoenergy.auth.domain.repository.UserRepository
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
) {

    /**
     * Creates a new user with the given username and password.
     * It saves hashed password and salt in the repository
     * @param username the username of the new user
     * @param password the password of the new user
     * @param repeatPassword the password of the new user
     * @return the id of the new user
     */
    suspend fun createUser(
        username: String,
        password: String,
        repeatPassword: String,
    ): UserCreationResult = either {
        ensure(username.length in 6..15) { UserCreationError.UserIsInvalid }
        ensure(password == repeatPassword) { UserCreationError.PasswordMismatch }
        ensure(isSafePassword(password)) { UserCreationError.InsecurePassword }

        dbQuery {
            ensure(!userRepository.userExistByUserName(username)) { UserCreationError.UserAlreadyExists }
            val saltedHash = hashingService.generateSaltedHash(password, 16)
            userRepository.createUser(username, saltedHash.hash, saltedHash.salt)
        }
    }

    /**
     * Creates a token for the given username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return the token
     */
    suspend fun createToken(
        username: String,
        password: String,
    ): TokenCreationResult = either {
        ensure(username.isNotBlank() && password.isNotBlank()) { TokenCreationError.UserOrPasswordAreInvalid }

        dbQuery {
            // Ensure user exists and password is correct
            val user = userRepository.getUserByUsername(username)
            ensureNotNull(user) { TokenCreationError.UserOrPasswordAreInvalid }
            val passwordIsValid = hashingService.matches(password, SaltedHash(user.password, user.salt))
            ensure(passwordIsValid) { TokenCreationError.UserOrPasswordAreInvalid }
            // Generate token
            tokenService.generateToken(user.id)
        }
    }
}

typealias UserCreationResult = Either<UserCreationError, Int>
typealias TokenCreationResult = Either<TokenCreationError, Token>

sealed interface UserCreationError {
    data object UserAlreadyExists : UserCreationError
    data object InsecurePassword : UserCreationError
    data object PasswordMismatch : UserCreationError
    data object UserIsInvalid : UserCreationError
}

sealed interface TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError
}

/**
 * Checks if the password is safe.
 * A safe password must have at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character.
 * @param password the password to check
 * @return true if the password is safe, false otherwise
 * */
fun isSafePassword(password: String): Boolean {
    val charPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
    val pattern: Pattern = Pattern.compile(charPattern)
    val matcher: Matcher = pattern.matcher(password)
    return matcher.matches()
}
