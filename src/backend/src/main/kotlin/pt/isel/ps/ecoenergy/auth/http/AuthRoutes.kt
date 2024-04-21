package pt.isel.ps.ecoenergy.auth.http

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
import pt.isel.ps.ecoenergy.Uris
import pt.isel.ps.ecoenergy.auth.domain.service.RoleReadingError
import pt.isel.ps.ecoenergy.auth.domain.service.UserCreationError
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
import pt.isel.ps.ecoenergy.auth.http.model.LoginRequest
import pt.isel.ps.ecoenergy.auth.http.model.LoginResponse
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.RoleRequest
import pt.isel.ps.ecoenergy.auth.http.model.SignUpRequest
import pt.isel.ps.ecoenergy.auth.http.model.respondProblem

fun Route.authRoutes(userService: UserService) {

    post(Uris.AUTH_SIGNUP) {
        val input = call.receive<SignUpRequest>()
        val res = userService.createUser(input.username, input.password, input.repeatPassword)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                // todo call.response.header("Location", "${Uris.USERS}/${res.value}")
            }

            is Left -> when (res.value) {
                UserCreationError.InsecurePassword -> call.respondProblem(Problem.insecurePassword, HttpStatusCode.BadRequest)
                UserCreationError.PasswordMismatch -> call.respondProblem(Problem.passwordMismatch, HttpStatusCode.BadRequest)
                UserCreationError.UserAlreadyExists -> call.respondProblem(Problem.userAlreadyExists, HttpStatusCode.Conflict)
                UserCreationError.UserIsInvalid -> call.respondProblem(Problem.userIsInvalid, HttpStatusCode.BadRequest)
            }
        }
    }
    route(Uris.AUTH_LOGIN) {
        post {
            val input = call.receive<LoginRequest>()
            val res = userService.createToken(input.username, input.password)

            when (res) {
                is Right -> call.respond(LoginResponse.fromToken(res.value))
                is Left -> call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.Forbidden)
            }
        }
    }
    route(Uris.USER_CHANGE_PASSWORD) {
        post {


        }
    }

    authenticate {
        route(Uris.USERS_ROLES) {
            get {
                val uid = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)

                val res = userService.getUserRoles(uid)

                when (res) {
                    is Right -> call.respond(res.value)
                    is Left -> when (res.value) {
                        RoleReadingError.UserNotFound -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
                    }
                }
            }

            post {
                val uid = call.parameters["id"]?.toIntOrNull()
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
                val uid = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.NotFound)
                val roleId = call.parameters["role-id"]
                    ?: return@delete call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.NotFound)

                val res = userService.deleteRole(uid, roleId)

                when (res) {
                    is Right -> call.respond(HttpStatusCode.NoContent)
                    is Left -> call.respondProblem(Problem.userOrPasswordAreInvalid, HttpStatusCode.Forbidden)
                }
            }
        }
    }
}

