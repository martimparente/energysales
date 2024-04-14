package pt.isel.ps.ecoenergy.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import pt.isel.ps.ecoenergy.auth.authRoutes
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
import pt.isel.ps.ecoenergy.common.Problem
import pt.isel.ps.ecoenergy.common.respond

fun Application.configureRouting(userService: UserService) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, Problem.internalServerError)
            //call.respondText(text = "My 500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }



    routing {
        authRoutes(userService)

        get("/") {
            call.respondText("Hello1, world!")
        }
    }
}
