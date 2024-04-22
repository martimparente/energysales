package pt.isel.ps.ecoenergy.auth.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.ecoenergy.auth.data.UserRepository
import pt.isel.ps.ecoenergy.auth.domain.model.SaltedHash
import pt.isel.ps.ecoenergy.auth.domain.model.Token
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
) {
    companion object {
        const val SALT_NUM_OF_BYTES = 16
    }

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
    ): UserCreationResult =
        either {
            ensure(username.length in 6..15) { UserCreationError.UserIsInvalid }
            ensure(password == repeatPassword) { UserCreationError.PasswordMismatch }
            ensure(isSafePassword(password)) { UserCreationError.InsecurePassword }
            ensure(!userRepository.userExists(username)) { UserCreationError.UserAlreadyExists }

            // todo what happens if the user suddenly exists? Transaction?
            val saltedHash = hashingService.generateSaltedHash(password, SALT_NUM_OF_BYTES)
            userRepository.createUser(username, saltedHash.hash, saltedHash.salt)
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
    ): TokenCreationResult =
        either {
            ensure(username.isNotBlank() && password.isNotBlank()) { TokenCreationError.UserOrPasswordAreInvalid }
            // Ensure user exists and password is correct
            val user = userRepository.getUserByUsername(username)
            ensureNotNull(user) { TokenCreationError.UserOrPasswordAreInvalid }
            val passwordIsValid = hashingService.matches(password, SaltedHash(user.password, user.salt))
            ensure(passwordIsValid) { TokenCreationError.UserOrPasswordAreInvalid }
            // Generate token
            tokenService.generateToken(user.id)
        }

    suspend fun changeUserPassword(
        uid: Int,
        oldPassword: String,
        newPassword: String,
        repeatNewPassword: String,
    ): ChangeUserPasswordResult =
        either {
            ensure(newPassword == repeatNewPassword) { ChangeUserPasswordError.PasswordMismatch }
            ensure(isSafePassword(newPassword)) { ChangeUserPasswordError.InsecurePassword }
            val user = userRepository.getUserById(uid)
            ensureNotNull(user) { ChangeUserPasswordError.UserOrPasswordAreInvalid }
            val passwordIsValid = hashingService.matches(oldPassword, SaltedHash(user.password, user.salt))
            ensure(passwordIsValid) { ChangeUserPasswordError.UserOrPasswordAreInvalid }
            val newSaltedHash = hashingService.generateSaltedHash(newPassword, SALT_NUM_OF_BYTES)
            val updatedUser = user.copy(password = newSaltedHash.hash, salt = newSaltedHash.salt)
            userRepository.updateUser(updatedUser)
        }

    suspend fun getUserRoles(uid: Int): RoleReadingResult =
        either {
            ensureNotNull(userRepository.getUserById(uid)) { RoleReadingError.UserNotFound }
            userRepository.getUserRoles(uid)
        }

    suspend fun assignRole(
        uid: Int,
        role: String,
    ): RoleAssignResult =
        either {
            ensureNotNull(userRepository.getUserById(uid)) { RoleAssignError.UserDoesNotExist }
            userRepository.assignRoleToUser(uid, role)
        }

    suspend fun deleteRole(
        uid: Int,
        role: String,
    ): RoleDeleteResult =
        either {
            ensureNotNull(userRepository.getUserById(uid)) { RoleDeletingError.UserNotFound }
            ensure(userRepository.getUserRoles(uid).contains(role)) { RoleDeletingError.UserHasNoRole }
            userRepository.deleteRoleFromUser(uid, role)
        }
}

typealias UserCreationResult = Either<UserCreationError, Int>
typealias TokenCreationResult = Either<TokenCreationError, Token>
typealias ChangeUserPasswordResult = Either<ChangeUserPasswordError, Boolean>
typealias RoleAssignResult = Either<RoleAssignError, Unit>
typealias RoleReadingResult = Either<RoleReadingError, List<String>>
typealias RoleDeleteResult = Either<RoleDeletingError, Unit>

sealed interface UserCreationError {
    data object UserAlreadyExists : UserCreationError

    data object InsecurePassword : UserCreationError

    data object PasswordMismatch : UserCreationError

    data object UserIsInvalid : UserCreationError
}

sealed interface TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError
}

sealed interface ChangeUserPasswordError {
    data object UserOrPasswordAreInvalid : ChangeUserPasswordError

    data object InsecurePassword : ChangeUserPasswordError

    data object PasswordMismatch : ChangeUserPasswordError
}

sealed interface RoleReadingError {
    data object UserNotFound : RoleReadingError
}

sealed interface RoleAssignError {
    data object UserDoesNotExist : RoleAssignError

    data object UserAlreadyHasRole : RoleAssignError

    data object UserDoesNotHaveRole : RoleAssignError
}

sealed interface RoleDeletingError {
    data object UserNotFound : RoleDeletingError

    data object UserHasNoRole : RoleDeletingError
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
