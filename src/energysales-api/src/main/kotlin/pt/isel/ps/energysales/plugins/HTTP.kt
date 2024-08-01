package pt.isel.ps.energysales.plugins

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import pt.isel.ps.energysales.users.AuthenticationException
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem

fun Application.configureHTTP() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    install(StatusPages) {
        /*  todo remove comments
            exception<Throwable> { call, _ ->
                 call.respondProblem(Problem.internalServerError, HttpStatusCode.InternalServerError)
             }*/
        exception<AuthenticationException> { call, _ ->
            call.respondProblem(Problem.unauthorized, HttpStatusCode.Unauthorized)
            // call.respond(status = HttpStatusCode.Unauthorized, message = cause.message ?: "Authentication failed!")
        }
        exception<BadRequestException> { call, _ ->
            call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)
            // call.respond(status = HttpStatusCode.BadRequest, message = cause.message ?: "Bad request!")
        }
        exception<NumberFormatException> { call, _ ->
            call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)
            // call.respond(status = HttpStatusCode.BadRequest, message = cause.message ?: "Bad request!")
        }
    }

    install(CORS) {
        anyHost() // This allows any host to access your API, which is fine for development but should be restricted in production.
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowSameOrigin = true
        // allow all methods
        HttpMethod.DefaultMethods.forEach {
            allowMethod(it)
        }
        allowHeaders { true } // Allow all headers
    }
}
