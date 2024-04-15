package pt.isel.ps.ecoenergy

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import pt.isel.ps.ecoenergy.auth.configureAuth
import pt.isel.ps.ecoenergy.auth.data.PsqlUserRepository
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
import pt.isel.ps.ecoenergy.auth.domain.service.security.JwtConfig
import pt.isel.ps.ecoenergy.auth.domain.service.security.JwtTokenService
import pt.isel.ps.ecoenergy.auth.domain.service.security.SHA256HashingService
import pt.isel.ps.ecoenergy.auth.http.authRoutes
import pt.isel.ps.ecoenergy.plugins.configureDatabases
import pt.isel.ps.ecoenergy.plugins.configureHTTP
import pt.isel.ps.ecoenergy.plugins.configureSerialization

fun main(args: Array<String>) = EngineMain.main(args)

// Todo check behaviour nested transactions w\ suspendTransactions

fun Application.module() {
    fun configProperty(propertyName: String) = environment.config.property(propertyName).getString()
    val jwtConfig =
        JwtConfig(configProperty("jwt.issuer"), configProperty("jwt.audience"), configProperty("jwt.realm"), configProperty("jwt.secret"))
    val userService by lazy {
        UserService(
            userRepository = PsqlUserRepository(null),
            tokenService = JwtTokenService(jwtConfig),
            hashingService = SHA256HashingService()
        )
    }
    configureDatabases()
    configureAuth(jwtConfig)
    configureHTTP()
    configureSerialization()

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    routing {
        authRoutes(userService)

        authenticate {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }
}
