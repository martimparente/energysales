package pt.isel.ps.ecoenergy.auth

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import pt.isel.ps.ecoenergy.auth.domain.service.UserCreationError
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
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
                UserCreationError.InsecurePassword -> call.respond(HttpStatusCode.Forbidden, Problem.insecurePassword)
                UserCreationError.PasswordDontMatch -> call.respond(HttpStatusCode.BadRequest, Problem.passwordDontMatch)
                UserCreationError.UserAlreadyExists -> call.respond(HttpStatusCode.Conflict, Problem.userAlreadyExists)
                UserCreationError.UserIsInvalid -> call.respond(HttpStatusCode.BadRequest, Problem.userIsInvalid)
            }
        }
    }

    route(Uris.AUTH_LOGIN) {
        get {
            call.respondText("get login")
        }

        post {
            call.respondText("post login")
        }
    }
}

