package pt.isel.ps.ecoenergy.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import pt.isel.ps.ecoenergy.auth.AuthenticationException
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.respondProblem

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
    }
    // will send this header with each response
    install(DefaultHeaders) {
        header("Content-Type", ContentType.Application.Json.toString())
    }

    install(CORS) {
        anyHost()
        allowHeaders { true }
        HttpMethod.DefaultMethods.forEach { method(it) }
    }
}

fun method(options: HttpMethod) {
}
