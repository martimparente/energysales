package pt.isel.ps.ecoenergy.auth.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import pt.isel.ps.ecoenergy.auth.domain.model.SaltedHash
import pt.isel.ps.ecoenergy.auth.domain.model.Token
import pt.isel.ps.ecoenergy.auth.domain.model.User
import pt.isel.ps.ecoenergy.auth.domain.repository.UserRepository
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery
import java.util.regex.Matcher
import java.util.regex.Pattern

fun isSafePassword(password: String): Boolean {
    val charPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
    val pattern: Pattern = Pattern.compile(charPattern)
    val matcher: Matcher = pattern.matcher(password)
    return matcher.matches()
}

class UserService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
) {

    suspend fun createUser(
        username: String,
        password: String,
        repeatPassword: String,
    ): UserCreationResult = either {
        ensure(username.length in 6..15) { UserCreationError.UserIsInvalid }
        ensure(password == repeatPassword) { UserCreationError.PasswordDontMatch }
        ensure(isSafePassword(password)) { UserCreationError.InsecurePassword }

        // Todo check behaviour nested transactions w\ suspendTransactions
        dbQuery {
            ensure(!userRepository.userExistByUserName(username)) { UserCreationError.UserAlreadyExists }
            val saltedHash = hashingService.generateSaltedHash(password, 16)
            userRepository.createUser(username, saltedHash.hash, saltedHash.salt)
        }
    }


    suspend fun createToken(
        username: String,
        password: String,
    ): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            return Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
        }
        // validate user and password
        return dbQuery {
            val user: User =
                userRepository.getUserByUsername(username)
                    ?: return@dbQuery Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            if (!hashingService.matches(password, SaltedHash(user.password, user.salt))) {
                return@dbQuery Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            } else {
                val token = tokenService.generateToken(user.id)
                return@dbQuery Either.Right(token)
            }
        }
    }

    /*
        fun getUserByUsername(username: String): UserInfoResult {
            return transactionManager.run {
                val usersRepository = it.usersRepository
                val user: User = usersRepository.getUserByUsername(username)
                    ?: return@run Either.Left(UserInfoError.UserNotFound)
                return@run Either.Right(user)
            }
        }*/
}

typealias UserCreationResult = Either<UserCreationError, Int>
typealias TokenCreationResult = Either<TokenCreationError, Token>
typealias UserInfoResult = Either<UserInfoError, User>

sealed interface UserCreationError {
    data object UserAlreadyExists : UserCreationError
    data object InsecurePassword : UserCreationError
    data object PasswordDontMatch : UserCreationError
    data object UserIsInvalid : UserCreationError
}

sealed class TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError()
}

sealed class UserInfoError {
    data object UserNotFound : UserInfoError()
}
