package pt.isel.ps.ecoenergy

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
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
import pt.isel.ps.ecoenergy.products.data.PsqlProductRepository
import pt.isel.ps.ecoenergy.products.domain.service.ProductService
import pt.isel.ps.ecoenergy.products.http.productRoutes
import pt.isel.ps.ecoenergy.sellers.data.PsqlSellerRepository
import pt.isel.ps.ecoenergy.sellers.domain.service.SellerService
import pt.isel.ps.ecoenergy.sellers.http.sellerRoutes
import pt.isel.ps.ecoenergy.teams.data.PsqlTeamRepository
import pt.isel.ps.ecoenergy.teams.domain.service.TeamService
import pt.isel.ps.ecoenergy.teams.http.teamRoutes

fun main(args: Array<String>) = EngineMain.main(args)

// Todo check behaviour nested transactions w\ suspendTransactions

fun Application.module() {
    fun configProperty(propertyName: String) = environment.config.property(propertyName).getString()
    val jwtConfig =
        JwtConfig(configProperty("jwt.issuer"), configProperty("jwt.audience"), configProperty("jwt.realm"), configProperty("jwt.secret"))
    val userService by lazy {
        UserService(
            userRepository = PsqlUserRepository(),
            tokenService = JwtTokenService(jwtConfig),
            hashingService = SHA256HashingService(),
        )
    }

    val teamService by lazy {
        TeamService(
            teamRepository = PsqlTeamRepository(),
        )
    }
    val sellerService by lazy {
        SellerService(
            sellerRepository = PsqlSellerRepository(),
        )
    }
    val productService by lazy {
        ProductService(
            productRepository = PsqlProductRepository(),
        )
    }

    install(Resources)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    configureSerialization()
    configureDatabases()
    configureAuth(jwtConfig)
    configureHTTP()

    routing {
        route(Uris.API) {
            authRoutes(userService)

            authenticate {
                teamRoutes(teamService)
                sellerRoutes(sellerService)
                productRoutes(productService)
                get(Uris.HOME) {
                    call.respondText("Hello, world!")
                }
            }
        }
    }
}