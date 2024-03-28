package pt.isel.ps

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import pt.isel.ps.plugins.configureDatabases
import pt.isel.ps.plugins.configureHTTP
import pt.isel.ps.plugins.configureMonitoring
import pt.isel.ps.plugins.configureRouting
import pt.isel.ps.plugins.configureSecurity
import pt.isel.ps.plugins.configureSerialization

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()

    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}
