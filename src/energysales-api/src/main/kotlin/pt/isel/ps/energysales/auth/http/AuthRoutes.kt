package pt.isel.ps.energysales.auth.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.domain.service.ChangeUserPasswordError
import pt.isel.ps.energysales.auth.domain.service.ResetPasswordError
import pt.isel.ps.energysales.auth.domain.service.RoleReadingError
import pt.isel.ps.energysales.auth.domain.service.UserCreationError
import pt.isel.ps.energysales.auth.domain.service.UserService
import pt.isel.ps.energysales.auth.http.model.ChangePasswordRequest
import pt.isel.ps.energysales.auth.http.model.CreateUserRequest
import pt.isel.ps.energysales.auth.http.model.LoginRequest
import pt.isel.ps.energysales.auth.http.model.LoginResponse
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.ResetPasswordRequest
import pt.isel.ps.energysales.auth.http.model.RoleRequest
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.plugins.authorized

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

    /**
     * Routes that require authentication and admin role
     */
    authenticate {
        authorized("ADMIN") {
            post(Uris.AUTH_SIGNUP) {
                val body = call.receive<CreateUserRequest>()
                val res = userService.createUser(body.username, body.password, body.repeatPassword, body.roles)

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

            route(Uris.USERS_ROLES) {
                get {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@get call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)

                    val res = userService.getUserRoles(uid)

                    when (res) {
                        is Right -> call.respond(res.value)
                        is Left ->
                            when (res.value) {
                                RoleReadingError.UserNotFound -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                            }
                    }
                }

                post {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@post call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                    val body = call.receive<RoleRequest>()

                    val res = userService.assignRole(uid, body.role)

                    when (res) {
                        is Right -> call.response.status(HttpStatusCode.Created)
                        is Left -> call.respondProblem(Problem.userNotFound, HttpStatusCode.Forbidden)
                    }
                }
            }

            route(Uris.USERS_ROLE) {
                delete {
                    val uid =
                        call.parameters["id"]?.toIntOrNull()
                            ?: return@delete call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.NotFound)
                    val roleName =
                        call.parameters["role-name"]
                            ?: return@delete call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.NotFound)

                    val res = userService.deleteRole(uid, roleName)

                    when (res) {
                        is Right -> call.respond(HttpStatusCode.NoContent)
                        is Left -> call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.Forbidden)
                    }
                }
            }
        }
    }
}
