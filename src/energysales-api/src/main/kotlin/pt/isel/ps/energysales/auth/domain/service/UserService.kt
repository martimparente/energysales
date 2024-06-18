package pt.isel.ps.energysales.auth.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.auth.data.UserRepository
import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.SaltedHash
import pt.isel.ps.energysales.auth.domain.model.User
import pt.isel.ps.energysales.auth.domain.model.UserCredentials
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
        name: String,
        surname: String,
        email: String,
        role: Role,
    ): Either<UserCreationError, Unit> =
        either {
            ensure(name.length in 2..16) { UserCreationError.UserNameIsInvalid }
            ensure(surname.length in 2..16) { UserCreationError.UserSurnameIsInvalid }
            ensure(isValidEmail(email)) { UserCreationError.UserEmailIsInvalid }
            ensure(username.length in 5..16) { UserCreationError.UserIsInvalid }
            ensure(password == repeatPassword) { UserCreationError.PasswordMismatch }
            ensure(isSafePassword(password)) { UserCreationError.InsecurePassword }
            ensure(userRepository.isEmailAvailable(email)) { UserCreationError.UserAlreadyExists }
            ensure(!userRepository.userExists(username)) { UserCreationError.UserAlreadyExists }
            // todo role ensure

            // todo what happens if the user suddenly exists? Transaction?
            val saltedHash = hashingService.generateSaltedHash(password, SALT_NUM_OF_BYTES)
            val user = User(-1, name, surname, email, role)
            val userCredentials = UserCredentials(-1, username, saltedHash.hash, saltedHash.salt)
            userRepository.createUser(user, userCredentials)
        }

    /**
     * Creates a token for the given username and password.
     *
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

            val credentials = userRepository.getUserCredentialsByUsername(username)
            ensureNotNull(credentials) { TokenCreationError.UserOrPasswordAreInvalid }
            val passwordIsValid = hashingService.matches(password, SaltedHash(credentials.password, credentials.salt))
            ensure(passwordIsValid) { TokenCreationError.UserOrPasswordAreInvalid }
            val role = userRepository.getUserRole(credentials.id)
            // Generate token TODO HARDCODED EXPIRATION TIME
            tokenService.generateJwtToken(credentials.username, role.toString(), 3600000)
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
            val credentials = userRepository.getUserCredentialsById(uid.toString())
            ensureNotNull(credentials) { ChangeUserPasswordError.UserOrPasswordAreInvalid }
            val passwordIsValid = hashingService.matches(oldPassword, SaltedHash(credentials.password, credentials.salt))
            ensure(passwordIsValid) { ChangeUserPasswordError.UserOrPasswordAreInvalid }
            val newSaltedHash = hashingService.generateSaltedHash(newPassword, SALT_NUM_OF_BYTES)
            val updatedCredentials = credentials.copy(password = newSaltedHash.hash, salt = newSaltedHash.salt)
            userRepository.updateUserCredentials(updatedCredentials)
        }

    suspend fun getUserRole(uid: Int): RoleReadingResult =
        either {
            ensureNotNull(userRepository.getUserById(uid)) { RoleReadingError.UserNotFound }
            val roleFound = userRepository.getUserRole(uid)
            // TODO
            ensureNotNull(roleFound) { RoleReadingError.UserNotFound }
        }

    suspend fun changeUserRole(
        uid: Int,
        role: String,
    ): RoleAssignResult =
        either {
            ensureNotNull(userRepository.getUserById(uid)) { RoleAssignError.UserDoesNotExist }
            userRepository.changeUserRole(uid, role)
        }

    fun resetPassword(email: String): Either<ResetPasswordError, Unit> =
        either {
            ensure(isValidEmail(email)) { ResetPasswordError.EmailIsInvalid }
            // val user = userRepository.getUserByEmail(email)
            // ensureNotNull(user) { ResetPasswordError.EmailNotFound }

            // send email with reset password link
            // todo
        }
}

typealias UserCreationResult = Either<UserCreationError, Int>
typealias TokenCreationResult = Either<TokenCreationError, String>
typealias ChangeUserPasswordResult = Either<ChangeUserPasswordError, Boolean>
typealias ResetPasswordResult = Either<ResetPasswordError, Boolean>

typealias RoleAssignResult = Either<RoleAssignError, Unit>
typealias RoleReadingResult = Either<RoleReadingError, Role>
typealias RoleDeleteResult = Either<RoleDeletingError, Unit>

sealed interface UserCreationError {
    data object UserAlreadyExists : UserCreationError

    data object InsecurePassword : UserCreationError

    data object PasswordMismatch : UserCreationError

    data object UserIsInvalid : UserCreationError

    data object UserInfoIsInvalid : UserCreationError

    data object UserNameIsInvalid : UserCreationError

    data object UserSurnameIsInvalid : UserCreationError

    data object UserEmailIsInvalid : UserCreationError
}

sealed interface TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError
}

sealed interface ChangeUserPasswordError {
    data object UserOrPasswordAreInvalid : ChangeUserPasswordError

    data object InsecurePassword : ChangeUserPasswordError

    data object PasswordMismatch : ChangeUserPasswordError
}

sealed interface ResetPasswordError {
    data object EmailNotFound : ResetPasswordError

    data object EmailIsInvalid : ResetPasswordError

    data object ResetEmailSendingError : ResetPasswordError
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

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
