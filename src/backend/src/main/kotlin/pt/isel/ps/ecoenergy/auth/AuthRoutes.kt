package pt.isel.ps.ecoenergy.auth

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import pt.isel.ps.ecoenergy.auth.domain.service.UserCreationError
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
import pt.isel.ps.ecoenergy.auth.http.model.LoginRequest
import pt.isel.ps.ecoenergy.auth.http.model.SignUpRequest
import pt.isel.ps.ecoenergy.common.Problem
import pt.isel.ps.ecoenergy.common.Uris
import pt.isel.ps.ecoenergy.common.respond

fun Route.authRoutes(userService: UserService) {

    post(Uris.AUTH_SIGNUP) {
        val input = call.receive<SignUpRequest>()
        val res = userService.createUser(input.username, input.password, input.repeatPassword)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.USERS}/${res.value}")
            }

            is Left -> when (res.value) {
                UserCreationError.InsecurePassword -> call.respond(Problem.insecurePassword, HttpStatusCode.BadRequest)
                UserCreationError.PasswordMisMatch -> call.respond(Problem.passwordMismatch, HttpStatusCode.BadRequest)
                UserCreationError.UserAlreadyExists -> call.respond(Problem.userAlreadyExists, HttpStatusCode.Conflict)
                UserCreationError.UserIsInvalid -> call.respond(Problem.userIsInvalid, HttpStatusCode.BadRequest)
            }
        }
    }

    route(Uris.AUTH_LOGIN) {
        post {
            val input = call.receive<LoginRequest>()
            val res = userService.createToken(input.username, input.password)

            when (res) {
                is Right -> call.respond(res.value)
                is Left -> call.respond(Problem.userOrPasswordAreInvalid, HttpStatusCode.Forbidden)
            }
        }
    }
}

