package pt.isel.ps.energysales.users.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.users.domain.model.toRole
import pt.isel.ps.energysales.users.domain.service.ChangeUserPasswordError
import pt.isel.ps.energysales.users.domain.service.ResetPasswordError
import pt.isel.ps.energysales.users.domain.service.RoleReadingError
import pt.isel.ps.energysales.users.domain.service.UserCreationError
import pt.isel.ps.energysales.users.domain.service.UserService
import pt.isel.ps.energysales.users.http.model.ChangePasswordRequest
import pt.isel.ps.energysales.users.http.model.CreateUserRequest
import pt.isel.ps.energysales.users.http.model.LoginRequest
import pt.isel.ps.energysales.users.http.model.LoginResponse
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.ResetPasswordRequest
import pt.isel.ps.energysales.users.http.model.RoleRequest
import pt.isel.ps.energysales.users.http.model.UserJSON
import pt.isel.ps.energysales.users.http.model.respondProblem

data class UserQueryParams(
    val role: String?,
    val available: Boolean?,
)

@Resource(Uris.USERS)
class UserResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{userId}")
    class Id(
        val parent: UserResource = UserResource(),
        val id: Int,
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
            is Left -> call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.Forbidden)
        }
    }

    post(Uris.AUTH_RESET_PASSWORD) {
        val body = call.receive<ResetPasswordRequest>()
        val res = userService.resetPassword(body.email)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    ResetPasswordError.EmailIsInvalid -> TODO()
                    ResetPasswordError.EmailNotFound -> TODO()
                    ResetPasswordError.ResetEmailSendingError -> TODO()
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
            post(Uris.USERS) {
                val body = call.receive<CreateUserRequest>()
                val res =
                    userService.createUser(
                        body.username,
                        body.password,
                        body.repeatPassword,
                        body.name,
                        body.surname,
                        body.email,
                        body.role.toRole(),
                    )

                when (res) {
                    is Right -> {
                        call.response.status(HttpStatusCode.Created)
                        // todo call.response.header("Location", "${Uris.USERS}/${res.value}")
                    }

                    is Left ->
                        when (res.value) {
                            UserCreationError.InsecurePassword -> call.respondProblem(Problem.insecurePassword, HttpStatusCode.BadRequest)
                            UserCreationError.PasswordMismatch -> call.respondProblem(Problem.passwordMismatch, HttpStatusCode.BadRequest)
                            UserCreationError.UserAlreadyExists -> call.respondProblem(Problem.userAlreadyExists, HttpStatusCode.Conflict)
                            UserCreationError.UserIsInvalid -> call.respondProblem(Problem.userIsInvalid, HttpStatusCode.BadRequest)
                            UserCreationError.UserEmailIsInvalid ->
                                call.respondProblem(
                                    Problem.UserEmailIsInvalid,
                                    HttpStatusCode.BadRequest,
                                )

                            UserCreationError.UserInfoIsInvalid -> call.respondProblem(Problem.UserInfoIsInvalid, HttpStatusCode.BadRequest)
                            UserCreationError.UserNameIsInvalid -> call.respondProblem(Problem.UserNameIsInvalid, HttpStatusCode.BadRequest)
                            UserCreationError.UserSurnameIsInvalid ->
                                call.respondProblem(
                                    Problem.UserSurnameIsInvalid,
                                    HttpStatusCode.BadRequest,
                                )
                        }
                }
            }

            route(Uris.USER_CHANGE_PASSWORD) {
                post {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@post call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)
                    val body = call.receive<ChangePasswordRequest>()
                    val res = userService.changeUserPassword(uid, body.oldPassword, body.newPassword, body.repeatNewPassword)

                    when (res) {
                        is Right -> call.respond(HttpStatusCode.OK)
                        is Left ->
                            when (res.value) {
                                ChangeUserPasswordError.InsecurePassword ->
                                    call.respondProblem(
                                        Problem.insecurePassword,
                                        HttpStatusCode.BadRequest,
                                    )

                                ChangeUserPasswordError.PasswordMismatch ->
                                    call.respondProblem(
                                        Problem.passwordMismatch,
                                        HttpStatusCode.BadRequest,
                                    )

                                ChangeUserPasswordError.UserOrPasswordAreInvalid ->
                                    call.respondProblem(
                                        Problem.userOrPasswordAreInvalid,
                                        HttpStatusCode.Forbidden,
                                    )
                            }
                    }
                }
            }

            route(Uris.USERS_ROLE) {
                get {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@get call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)

                    val res = userService.getUserRole(uid)

                    when (res) {
                        is Right -> call.respond(res.value)
                        is Left ->
                            when (res.value) {
                                RoleReadingError.UserNotFound -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                            }
                    }
                }

                put {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@put call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                    val body = call.receive<RoleRequest>()

                    val res = userService.changeUserRole(uid, body.role)

                    when (res) {
                        is Right -> call.response.status(HttpStatusCode.Created)
                        is Left -> call.respondProblem(Problem.userNotFound, HttpStatusCode.Forbidden)
                    }
                }
            }

            // ROUTE TO get manager candidates for a team, query parameters are "role" and "available"
            get(Uris.USERS) {
                val params =
                    UserQueryParams(
                        call.request.queryParameters["role"],
                        call.request.queryParameters["available"]?.toBoolean(),
                    )

                val res = userService.getUsers(params)

                when (res) {
                    is Right -> {
                        val managers = res.value.map { UserJSON.fromUser(it) }
                        call.respond(managers)
                    }

                    is Left -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                }
            }

            // Get user by id
            get(Uris.USERS_BY_ID) {
                val uid =
                    call.parameters["userId"]?.toIntOrNull()
                        ?: return@get call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)

                val res = userService.getUser(uid)

                when (res) {
                    is Right -> call.respond(UserJSON.fromUser(res.value))
                    is Left -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                }
            }
        }
    }
}
