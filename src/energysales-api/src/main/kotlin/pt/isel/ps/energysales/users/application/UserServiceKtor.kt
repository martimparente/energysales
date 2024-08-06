package pt.isel.ps.energysales.users.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.email.MailService
import pt.isel.ps.energysales.users.application.dto.ChangeUserPasswordError
import pt.isel.ps.energysales.users.application.dto.ChangeUserPasswordResult
import pt.isel.ps.energysales.users.application.dto.CreateUserInput
import pt.isel.ps.energysales.users.application.dto.DeleteUserResult
import pt.isel.ps.energysales.users.application.dto.GetUserError
import pt.isel.ps.energysales.users.application.dto.GetUsersResult
import pt.isel.ps.energysales.users.application.dto.ResetPasswordError
import pt.isel.ps.energysales.users.application.dto.ResetPasswordResult
import pt.isel.ps.energysales.users.application.dto.RoleAssignError
import pt.isel.ps.energysales.users.application.dto.RoleAssignResult
import pt.isel.ps.energysales.users.application.dto.RoleReadingError
import pt.isel.ps.energysales.users.application.dto.RoleReadingResult
import pt.isel.ps.energysales.users.application.dto.TokenCreationError
import pt.isel.ps.energysales.users.application.dto.TokenCreationResult
import pt.isel.ps.energysales.users.application.dto.UpdateUserInput
import pt.isel.ps.energysales.users.application.dto.UpdateUserResult
import pt.isel.ps.energysales.users.application.dto.UserCreationError
import pt.isel.ps.energysales.users.application.dto.UserCreationResult
import pt.isel.ps.energysales.users.application.dto.UserDeletingError
import pt.isel.ps.energysales.users.application.dto.UserReadingResult
import pt.isel.ps.energysales.users.application.dto.UserUpdatingError
import pt.isel.ps.energysales.users.application.security.HashingService
import pt.isel.ps.energysales.users.application.security.TokenService
import pt.isel.ps.energysales.users.data.UserRepository
import pt.isel.ps.energysales.users.domain.SaltedHash
import pt.isel.ps.energysales.users.domain.User
import pt.isel.ps.energysales.users.domain.UserCredentials
import pt.isel.ps.energysales.users.domain.toRole
import pt.isel.ps.energysales.users.http.UserQueryParams
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserServiceKtor(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
    private val mailService: MailService,
) : UserService {
    companion object {
        const val SALT_NUM_OF_BYTES = 16
    }

    override suspend fun createUser(input: CreateUserInput): UserCreationResult =
        either {
            ensure(input.name.length in 2..16) { UserCreationError.UserNameIsInvalid }
            ensure(input.surname.length in 2..16) { UserCreationError.UserSurnameIsInvalid }
            ensure(input.username.length in 5..16) { UserCreationError.UserUsernameIsInvalid }
            ensure(input.password == input.repeatPassword) { UserCreationError.PasswordMismatch }
            ensureNotNull(input.role.toRole()) { UserCreationError.UserRoleIsInvalid }
            ensure(isValidEmail(input.email)) { UserCreationError.UserEmailIsInvalid }
            ensure(isSafePassword(input.password)) { UserCreationError.InsecurePassword }
            ensure(userRepository.isEmailAvailable(input.email)) { UserCreationError.UserEmailAlreadyUsed }
            ensure(!userRepository.userExists(input.username)) { UserCreationError.UserAlreadyExists }

            // todo what happens if the user suddenly exists? Transaction context needed...
            val saltedHash = hashingService.generateSaltedHash(input.password, SALT_NUM_OF_BYTES)
            val user = User(null, input.name, input.surname, input.email, input.role.toRole())
            val userCredentials = UserCredentials(null, input.username, saltedHash.hash, saltedHash.salt)
            userRepository.createUser(user, userCredentials)
        }

    override suspend fun createToken(
        username: String,
        password: String,
    ): TokenCreationResult =
        either {
            ensure(username.length in 5..16) { TokenCreationError.UserUsernameIsInvalid }
            ensure(isSafePassword(password)) { TokenCreationError.InsecurePassword }
            val credentials = userRepository.getUserCredentialsByUsername(username)
            ensureNotNull(credentials) { TokenCreationError.UserNotFound }
            val passwordIsValid = hashingService.matches(password, SaltedHash(credentials.pwHash, credentials.salt))
            ensure(passwordIsValid) { TokenCreationError.WrongPassword }
            val role = userRepository.getUserRole(credentials.id!!)
            // Generate token TODO HARDCODED EXPIRATION TIME
            tokenService.generateJwtToken(credentials.username, credentials.id.toString(), role.toString(), 3600000)
        }

    override suspend fun changeUserPassword(
        id: String,
        oldPassword: String,
        newPassword: String,
        repeatNewPassword: String,
    ): ChangeUserPasswordResult =
        either {
            ensure(newPassword == repeatNewPassword) { ChangeUserPasswordError.PasswordMismatch }
            ensure(isSafePassword(newPassword)) { ChangeUserPasswordError.InsecurePassword }
            val credentials = userRepository.getUserCredentialsById(id) ?: raise(ChangeUserPasswordError.UserNotFound)
            val passwordIsValid = hashingService.matches(oldPassword, SaltedHash(credentials.pwHash, credentials.salt))
            ensure(passwordIsValid) { ChangeUserPasswordError.WrongPassword }
            val newSaltedHash = hashingService.generateSaltedHash(newPassword, SALT_NUM_OF_BYTES)
            val updatedCredentials = credentials.copy(pwHash = newSaltedHash.hash, salt = newSaltedHash.salt)

            userRepository.updateUserCredentials(updatedCredentials)
        }

    override suspend fun resetPassword(email: String): ResetPasswordResult =
        either {
            ensure(isValidEmail(email)) { ResetPasswordError.EmailIsInvalid }
            val user = userRepository.getUserByEmail(email) ?: raise(ResetPasswordError.UserNotFound)

            when (mailService.sendResetPasswordEmail(email, user.name)) {
                is Either.Left -> raise(ResetPasswordError.ResetEmailSendingError)
                is Either.Right -> Unit
            }
        }

    override suspend fun getUserRole(id: String): RoleReadingResult =
        either {
            userRepository.getUserRole(id) ?: raise(RoleReadingError.UserNotFound)
        }

    override suspend fun changeUserRole(
        id: String,
        role: String,
    ): RoleAssignResult =
        either {
            ensureNotNull(userRepository.getUserById(id)) { RoleAssignError.UserNotFound }
            userRepository.changeUserRole(id, role.toRole().name)
        }

    override suspend fun getUsers(params: UserQueryParams): GetUsersResult =
        either {
            userRepository.getAll()
        }

    override suspend fun getUser(id: String): UserReadingResult =
        either {
            userRepository.getUserById(id) ?: raise(GetUserError.UserNotFound)
        }

    override suspend fun updateUser(input: UpdateUserInput): UpdateUserResult =
        either {
            ensure(input.name?.length in 2..16) { UserUpdatingError.UserNameIsInvalid }
            ensure(input.surname?.length in 2..16) { UserUpdatingError.UserSurnameIsInvalid }
            ensureNotNull(input.role?.toRole()) { UserUpdatingError.UserRoleIsInvalid }
            input.email?.let {
                ensure(isValidEmail(it)) { UserUpdatingError.UserEmailIsInvalid }
                ensure(userRepository.isEmailAvailable(input.email)) { UserUpdatingError.UserEmailAlreadyUsed }
            }

            val user = userRepository.getUserById(input.id) ?: raise(UserUpdatingError.UserNotFound)
            val patchedUser =
                user.copy(
                    name = input.name ?: user.name,
                    surname = input.surname ?: user.surname,
                    email = input.email ?: user.email,
                    role = input.role?.toRole() ?: user.role,
                )
            userRepository.updateUser(patchedUser) ?: raise(UserUpdatingError.Todo)
        }

    override suspend fun deleteUser(id: String): DeleteUserResult =
        either {
            val user = userRepository.getUserById(id) ?: raise(UserDeletingError.UserNotFound)
            userRepository.deleteUser(user.id!!)
        }
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
