package pt.isel.ps.ecoenergy.plugins

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.configureHTTP() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    // will send this header with each response
    install(DefaultHeaders) {
        header("Content-Type", ContentType.Application.Json.toString())
    }
}
