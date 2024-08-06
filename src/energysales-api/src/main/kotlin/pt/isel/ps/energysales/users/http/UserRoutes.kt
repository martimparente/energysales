package pt.isel.ps.energysales.users.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.plugins.respondProblem
import pt.isel.ps.energysales.users.application.UserService
import pt.isel.ps.energysales.users.application.dto.ChangeUserPasswordError
import pt.isel.ps.energysales.users.application.dto.CreateUserInput
import pt.isel.ps.energysales.users.application.dto.GetUserError
import pt.isel.ps.energysales.users.application.dto.GetUsersError
import pt.isel.ps.energysales.users.application.dto.ResetPasswordError
import pt.isel.ps.energysales.users.application.dto.RoleAssignError
import pt.isel.ps.energysales.users.application.dto.RoleReadingError
import pt.isel.ps.energysales.users.application.dto.TokenCreationError
import pt.isel.ps.energysales.users.application.dto.UpdateUserInput
import pt.isel.ps.energysales.users.application.dto.UserCreationError
import pt.isel.ps.energysales.users.application.dto.UserDeletingError
import pt.isel.ps.energysales.users.application.dto.UserUpdatingError
import pt.isel.ps.energysales.users.http.model.ChangePasswordRequest
import pt.isel.ps.energysales.users.http.model.CreateUserRequest
import pt.isel.ps.energysales.users.http.model.LoginRequest
import pt.isel.ps.energysales.users.http.model.LoginResponse
import pt.isel.ps.energysales.users.http.model.PatchUserRequest
import pt.isel.ps.energysales.users.http.model.ResetPasswordRequest
import pt.isel.ps.energysales.users.http.model.RoleRequest
import pt.isel.ps.energysales.users.http.model.UserJSON
import pt.isel.ps.energysales.users.http.model.UserProblem

data class UserQueryParams(
    val role: String?,
)

@Resource(Uris.USERS)
class UserResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: UserResource = UserResource(),
        val id: String,
    )
}

fun Route.authRoutes(userService: UserService) {
    /**
     * Routes that do not require authentication
     */

    post(Uris.AUTH_LOGIN) {
        val body = call.receive<LoginRequest>()
        val res = userService.createToken(body.username, body.password)

        when (res) {
            is Right -> call.respond(LoginResponse(res.value))
            is Left ->
                when (res.value) {
                    TokenCreationError.UserUsernameIsInvalid -> call.respondProblem(UserProblem.userOrPasswordAreInvalid)
                    TokenCreationError.UserNotFound -> call.respondProblem(UserProblem.userOrPasswordAreInvalid)
                    TokenCreationError.WrongPassword -> call.respondProblem(UserProblem.userOrPasswordAreInvalid)
                    TokenCreationError.InsecurePassword -> call.respondProblem(UserProblem.userOrPasswordAreInvalid)
                }
        }
    }

    post(Uris.AUTH_RESET_PASSWORD) {
        val body = call.receive<ResetPasswordRequest>()
        val res = userService.resetPassword(body.email)

        when (res) {
            is Right -> call.response.status(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    ResetPasswordError.EmailIsInvalid -> call.respondProblem(UserProblem.userNotFound)
                    ResetPasswordError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                    ResetPasswordError.ResetEmailSendingError -> call.respondProblem(UserProblem.internalServerError)
                }
        }
    }

    post(Uris.USER_CHANGE_PASSWORD) {
        val uid = call.parameters["id"] ?: return@post call.respondProblem(UserProblem.badRequest)
        val body = call.receive<ChangePasswordRequest>()
        val res = userService.changeUserPassword(uid, body.oldPassword, body.newPassword, body.repeatNewPassword)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    ChangeUserPasswordError.InsecurePassword -> call.respondProblem(UserProblem.insecurePassword)
                    ChangeUserPasswordError.PasswordMismatch -> call.respondProblem(UserProblem.passwordMismatch)
                    ChangeUserPasswordError.UserOrPasswordAreInvalid -> call.respondProblem(UserProblem.userOrPasswordAreInvalid)
                    ChangeUserPasswordError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                    ChangeUserPasswordError.WrongPassword -> call.respondProblem(UserProblem.wrongPassword)
                }
        }
    }
}

fun Route.userRoutes(userService: UserService) {
    /**
     * Routes that require authentication and admin role
     */
    authenticate {
        authorize("ADMIN") {
            route(Uris.USERS) {
                post {
                    val body = call.receive<CreateUserRequest>()
                    val input =
                        CreateUserInput(
                            body.username,
                            body.password,
                            body.repeatPassword,
                            body.name,
                            body.surname,
                            body.email,
                            body.role,
                        )
                    val res = userService.createUser(input)

                    when (res) {
                        is Right -> {
                            call.response.header("Location", "${Uris.USERS}/${res.value}")
                            call.response.status(HttpStatusCode.Created)
                        }

                        is Left ->
                            when (res.value) {
                                UserCreationError.InsecurePassword -> call.respondProblem(UserProblem.insecurePassword)
                                UserCreationError.PasswordMismatch -> call.respondProblem(UserProblem.passwordMismatch)
                                UserCreationError.UserAlreadyExists -> call.respondProblem(UserProblem.userAlreadyExists)
                                UserCreationError.UserIsInvalid -> call.respondProblem(UserProblem.userIsInvalid)
                                UserCreationError.UserEmailIsInvalid -> call.respondProblem(UserProblem.userEmailIsInvalid)
                                UserCreationError.UserInfoIsInvalid -> call.respondProblem(UserProblem.userInfoIsInvalid)
                                UserCreationError.UserNameIsInvalid -> call.respondProblem(UserProblem.userNameIsInvalid)
                                UserCreationError.UserSurnameIsInvalid -> call.respondProblem(UserProblem.userSurnameIsInvalid)
                                UserCreationError.UserEmailAlreadyUsed -> call.respondProblem(UserProblem.userEmailAlreadyUsed)
                                UserCreationError.UserRoleIsInvalid -> call.respondProblem(UserProblem.userRoleIsInvalid)
                                UserCreationError.UserUsernameIsInvalid -> call.respondProblem(UserProblem.userUsernameIsInvalid)
                            }
                    }
                }
                get {
                    val params = UserQueryParams(call.request.queryParameters["role"])
                    val res = userService.getUsers(params)

                    when (res) {
                        is Right -> {
                            val users = res.value.map { UserJSON.fromUser(it) }
                            call.respond(users)
                        }

                        is Left ->
                            when (res.value) {
                                GetUsersError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                                GetUserError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                            }
                    }
                }
            }

            route(Uris.USERS_BY_ID) {
                get {
                    val uid = call.parameters["id"] ?: return@get call.respondProblem(UserProblem.badRequest)
                    val res = userService.getUser(uid)

                    when (res) {
                        is Right -> call.respond(UserJSON.fromUser(res.value))
                        is Left ->
                            when (res.value) {
                                GetUsersError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                                GetUserError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                            }
                    }
                }
                patch {
                    val id = call.parameters["id"] ?: return@patch call.respondProblem(UserProblem.badRequest)
                    val body = call.receive<PatchUserRequest>()
                    val input = UpdateUserInput(id, body.name, body.surname, body.email, body.role)
                    val res = userService.updateUser(input)

                    when (res) {
                        is Right -> {
                            call.response.header("Location", "${Uris.USERS}/${res.value}")
                            call.response.status(HttpStatusCode.OK)
                        }

                        is Left ->
                            when (res.value) {
                                UserUpdatingError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                                UserUpdatingError.InsecurePassword -> call.respondProblem(UserProblem.insecurePassword)
                                UserUpdatingError.UserAlreadyExists -> call.respondProblem(UserProblem.userAlreadyExists)
                                UserUpdatingError.UserEmailAlreadyUsed -> call.respondProblem(UserProblem.userEmailAlreadyUsed)
                                UserUpdatingError.UserEmailIsInvalid -> call.respondProblem(UserProblem.userEmailIsInvalid)
                                UserUpdatingError.UserInfoIsInvalid -> call.respondProblem(UserProblem.userInfoIsInvalid)
                                UserUpdatingError.UserIsInvalid -> call.respondProblem(UserProblem.userIsInvalid)
                                UserUpdatingError.UserNameIsInvalid -> call.respondProblem(UserProblem.userNameIsInvalid)
                                UserUpdatingError.UserRoleIsInvalid -> call.respondProblem(UserProblem.userRoleIsInvalid)
                                UserUpdatingError.UserSurnameIsInvalid -> call.respondProblem(UserProblem.userSurnameIsInvalid)
                                UserUpdatingError.Todo -> call.respondProblem(UserProblem.internalServerError)
                            }
                    }
                }
                delete {
                    val uid = call.parameters["id"] ?: return@delete call.respondProblem(UserProblem.badRequest)
                    val res = userService.deleteUser(uid)

                    when (res) {
                        is Right -> call.response.status(HttpStatusCode.NoContent)
                        is Left ->
                            when (res.value) {
                                UserDeletingError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                            }
                    }
                }
            }

            route(Uris.USERS_ROLE) {
                get {
                    val uid = call.parameters["id"] ?: return@get call.respondProblem(UserProblem.badRequest)
                    val res = userService.getUserRole(uid)

                    when (res) {
                        is Right -> call.respond(res.value)
                        is Left ->
                            when (res.value) {
                                RoleReadingError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                            }
                    }
                }

                put {
                    val uid = call.parameters["id"] ?: return@put call.respondProblem(UserProblem.userNotFound)
                    val body = call.receive<RoleRequest>()
                    val res = userService.changeUserRole(uid, body.role)

                    when (res) {
                        is Right -> call.response.status(HttpStatusCode.Created)
                        is Left ->
                            when (res.value) {
                                RoleAssignError.RoleNotFound -> call.respondProblem(UserProblem.roleNotFound)
                                RoleAssignError.UserNotFound -> call.respondProblem(UserProblem.userNotFound)
                            }
                    }
                }
            }
        }
    }
}
