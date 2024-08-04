package pt.isel.ps.energysales.users.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class UserProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val insecurePassword =
            UserProblem(
                title = "Insecure Password",
                type = PROBLEM_URL + "insecurePassword.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail =
                "Passwords should have at least 8 characters and at least one of the following types:\n" +
                    "Uppercase letters: A-Z\n" +
                    "Lowercase letters: a-z\n" +
                    "Numbers: 0-9\n" +
                    "Symbols: ~`!@#\$%^&*()_-+={[:\"'<,>.?/",
            )

        val passwordMismatch =
            UserProblem(
                title = "Password Mismatch",
                type = PROBLEM_URL + "passwordMismatch.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Passwords don't match",
            )

        val userAlreadyExists =
            UserProblem(
                title = "User Already Exists",
                type = PROBLEM_URL + "UserAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A user with the specified username already exists",
            )

        val userIsInvalid =
            UserProblem(
                title = "Invalid User",
                type = PROBLEM_URL + "userIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "User must have between 6 to 15 characters",
            )

        val userEmailIsInvalid =
            UserProblem(
                title = "Invalid Email",
                type = PROBLEM_URL + "UserEmailIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided email is invalid",
            )

        val userInfoIsInvalid =
            UserProblem(
                title = "Invalid User Info",
                type = PROBLEM_URL + "UserInfoIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided user information is invalid",
            )

        val userNameIsInvalid =
            UserProblem(
                title = "Invalid Username",
                type = PROBLEM_URL + "UserNameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Username must have between 2 and 16 characters",
            )

        val userOrPasswordAreInvalid =
            UserProblem(
                title = "Username or Password invalid",
                type = PROBLEM_URL + "userOrPasswordAreInvalid.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The provided username or password is invalid",
            )

        val userSurnameIsInvalid =
            UserProblem(
                title = "Invalid User Surname",
                type = PROBLEM_URL + "UserSurnameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Surname must have between 2 and 16 characters",
            )

        val userEmailAlreadyUsed =
            UserProblem(
                title = "Email Already Used",
                type = PROBLEM_URL + "UserEmailAlreadyUsed.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "The provided email is already used by another user",
            )

        val userRoleIsInvalid =
            UserProblem(
                title = "Invalid User Role",
                type = PROBLEM_URL + "UserRoleIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided user role is invalid",
            )

        val userUsernameIsInvalid =
            UserProblem(
                title = "Invalid Username",
                type = PROBLEM_URL + "UserUsernameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Username must have between 5 and 16 characters",
            )

        // Additional problem definitions
        val userNotFound =
            UserProblem(
                title = "User Not Found",
                type = PROBLEM_URL + "userNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified user was not found",
            )

        val badRequest =
            UserProblem(
                title = "Bad Request",
                type = PROBLEM_URL + "badRequest.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The request was invalid or cannot be otherwise served",
            )

        val unauthorized =
            UserProblem(
                title = "Unauthorized",
                type = PROBLEM_URL + "unauthorized.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The request has not been applied because it lacks valid authentication credentials for the target resource",
            )

        val roleNotFound =
            UserProblem(
                title = "Role Not Found",
                type = PROBLEM_URL + "roleNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified role was not found",
            )

        val wrongPassword =
            UserProblem(
                title = "Wrong Password",
                type = PROBLEM_URL + "wrongPassword.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The provided password is incorrect",
            )
        val forbidden =
            UserProblem(
                title = "Forbidden",
                type = PROBLEM_URL + "forbidden.md",
                instance = "",
                status = HttpStatusCode.Forbidden,
                detail = "The request was a valid request, but the server is refusing to respond to it",
            )
        val internalServerError =
            UserProblem(
                title = "Internal Server Error",
                type = PROBLEM_URL + "InternalServerError.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "There was a server error :( Please try again later",
            )
    }
}
