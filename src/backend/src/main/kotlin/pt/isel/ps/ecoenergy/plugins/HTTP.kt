package pt.isel.ps.ecoenergy.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.respond

fun Application.configureHTTP() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    install(StatusPages) {
        exception<Throwable> { call, _ ->
            call.respond(Problem.internalServerError, HttpStatusCode.InternalServerError)
        }
    }
    // will send this header with each response
    install(DefaultHeaders) {
        header("Content-Type", ContentType.Application.Json.toString())
    }
}
