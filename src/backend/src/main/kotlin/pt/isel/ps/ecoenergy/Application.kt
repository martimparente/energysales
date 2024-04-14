package pt.isel.ps.ecoenergy

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import pt.isel.ps.ecoenergy.auth.data.repository.PsqlUserRepository
import pt.isel.ps.ecoenergy.auth.domain.service.UserService
import pt.isel.ps.ecoenergy.auth.security.JwtConfig
import pt.isel.ps.ecoenergy.auth.security.JwtTokenService
import pt.isel.ps.ecoenergy.auth.security.SHA256HashingService
import pt.isel.ps.ecoenergy.plugins.configureDatabases
import pt.isel.ps.ecoenergy.plugins.configureHTTP
import pt.isel.ps.ecoenergy.plugins.configureMonitoring
import pt.isel.ps.ecoenergy.plugins.configureRouting
import pt.isel.ps.ecoenergy.plugins.configureSecurity
import pt.isel.ps.ecoenergy.plugins.configureSerialization

fun main(args: Array<String>) = EngineMain.main(args)


fun Application.module() {

    fun getProperty(propertyName: String) = environment.config.property(propertyName).getString()
    val jwtConfig = JwtConfig(getProperty("jwt.issuer"), getProperty("jwt.audience"), getProperty("jwt.realm"), getProperty("jwt.secret"))

    configureDatabases()

    val userService by lazy {
        UserService(
            userRepository = PsqlUserRepository(null),
            tokenService = JwtTokenService(jwtConfig),
            hashingService = SHA256HashingService()
        )
    }

    configureSecurity(jwtConfig)
    configureRouting(userService)
    configureMonitoring()
    configureHTTP()
    configureSerialization()
}
