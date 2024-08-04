package pt.isel.ps.energysales.users.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.users.domain.Role
import pt.isel.ps.energysales.users.domain.User

typealias TokenCreationResult = Either<TokenCreationError, String>
typealias UserCreationResult = Either<UserCreationError, String>
typealias DeleteUserResult = Either<UserDeletingError, Unit>
typealias UpdateUserResult = Either<UserUpdatingError, User>
typealias GetUsersResult = Either<GetUsersError, List<User>>

typealias ChangeUserPasswordResult = Either<ChangeUserPasswordError, Boolean>
typealias ResetPasswordResult = Either<ResetPasswordError, Unit>

typealias RoleAssignResult = Either<RoleAssignError, Unit>
typealias RoleReadingResult = Either<RoleReadingError, Role>
typealias RoleDeleteResult = Either<RoleDeletingError, Unit>

typealias UserReadingResult = Either<GetUsersError, User>
typealias UsersReadingResult = Either<GetUsersError, List<User>>

sealed interface UserCreationError {
    data object UserUsernameIsInvalid : UserCreationError

    data object PasswordMismatch : UserCreationError

    data object UserNameIsInvalid : UserCreationError

    data object UserSurnameIsInvalid : UserCreationError

    data object UserAlreadyExists : UserCreationError

    data object InsecurePassword : UserCreationError

    data object UserIsInvalid : UserCreationError

    data object UserEmailAlreadyUsed : UserCreationError

    data object UserInfoIsInvalid : UserCreationError

    data object UserRoleIsInvalid : UserCreationError

    data object UserEmailIsInvalid : UserCreationError
}

sealed interface UserUpdatingError {
    data object UserNotFound : UserUpdatingError
}

sealed interface UserDeletingError {
    data object UserNotFound : UserDeletingError
}

sealed interface GetUsersError {
    data object UserNotFound : GetUsersError
}

sealed interface GetUserError {
    data object UserNotFound : GetUsersError
}

sealed interface TokenCreationError {
    data object UserUsernameIsInvalid : TokenCreationError

    data object InsecurePassword : TokenCreationError

    data object UserNotFound : TokenCreationError

    data object WrongPassword : TokenCreationError
}

sealed interface ChangeUserPasswordError {
    data object UserOrPasswordAreInvalid : ChangeUserPasswordError

    data object InsecurePassword : ChangeUserPasswordError

    data object PasswordMismatch : ChangeUserPasswordError

    data object UserNotFound : ChangeUserPasswordError

    data object WrongPassword : ChangeUserPasswordError
}

sealed interface ResetPasswordError {
    data object UserNotFound : ResetPasswordError

    data object EmailIsInvalid : ResetPasswordError

    data object ResetEmailSendingError : ResetPasswordError
}

sealed interface RoleReadingError {
    data object UserNotFound : RoleReadingError
}

sealed interface RoleAssignError {
    data object UserNotFound : RoleAssignError

    data object RoleNotFound : RoleAssignError
}

sealed interface RoleDeletingError {
    data object UserNotFound : RoleDeletingError

    data object UserHasNoRole : RoleDeletingError
}
