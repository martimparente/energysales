package pt.isel.ps

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import pt.isel.ps.plugins.configureDatabases
import pt.isel.ps.plugins.configureHTTP
import pt.isel.ps.plugins.configureMonitoring
import pt.isel.ps.plugins.configureRouting
import pt.isel.ps.plugins.configureSecurity
import pt.isel.ps.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
