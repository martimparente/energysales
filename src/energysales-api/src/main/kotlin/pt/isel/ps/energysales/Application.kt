package pt.isel.ps.energysales

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import pt.isel.ps.energysales.auth.configureAuth
import pt.isel.ps.energysales.auth.data.PsqlUserRepository
import pt.isel.ps.energysales.auth.domain.service.UserService
import pt.isel.ps.energysales.auth.domain.service.security.JwtConfig
import pt.isel.ps.energysales.auth.domain.service.security.JwtTokenService
import pt.isel.ps.energysales.auth.domain.service.security.SHA256HashingService
import pt.isel.ps.energysales.auth.http.authRoutes
import pt.isel.ps.energysales.clients.data.PsqlClientRepository
import pt.isel.ps.energysales.clients.domain.service.ClientService
import pt.isel.ps.energysales.clients.http.clientRoutes
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.plugins.configureDatabases
import pt.isel.ps.energysales.plugins.configureHTTP
import pt.isel.ps.energysales.plugins.configureSerialization
import pt.isel.ps.energysales.products.data.PsqlProductRepository
import pt.isel.ps.energysales.products.domain.service.ProductService
import pt.isel.ps.energysales.products.http.productRoutes
import pt.isel.ps.energysales.sellers.data.PsqlSellerRepository
import pt.isel.ps.energysales.sellers.domain.service.SellerService
import pt.isel.ps.energysales.sellers.http.sellerRoutes
import pt.isel.ps.energysales.teams.data.PsqlTeamRepository
import pt.isel.ps.energysales.teams.domain.service.TeamService
import pt.isel.ps.energysales.teams.http.teamRoutes

fun main(args: Array<String>) = EngineMain.main(args)

// Todo check behaviour nested transactions w\ suspendTransactions

fun Application.module() {
    /**
     * Configuration
     */

    fun configProperty(propertyName: String) = environment.config.property(propertyName).getString()
    val jwtConfig =
        JwtConfig(configProperty("jwt.issuer"), configProperty("jwt.audience"), configProperty("jwt.realm"), configProperty("jwt.secret"))

    /**
     * Services
     */

    val userService by lazy {
        UserService(
            userRepository = PsqlUserRepository(),
            tokenService = JwtTokenService(jwtConfig),
            hashingService = SHA256HashingService(),
        )
    }
    val teamService by lazy { TeamService(teamRepository = PsqlTeamRepository()) }
    val sellerService by lazy { SellerService(sellerRepository = PsqlSellerRepository()) }
    val productService by lazy { ProductService(productRepository = PsqlProductRepository()) }
    val clientService by lazy { ClientService(clientRepository = PsqlClientRepository()) }

    /**
     * Plugins
     */

    install(Resources)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    configureSerialization()
    configureDatabases()
    configureAuth(jwtConfig)
    configureHTTP()

    /**
     * Routes
     */
    routing {
        singlePageApplication {
            react("../energysales-spa/dist")
        }

        route(Uris.API) {
            authRoutes(userService)
            authenticate {
                authorize("ADMIN") {
                    teamRoutes(teamService)
                    sellerRoutes(sellerService)
                    productRoutes(productService)
                }
                authorize("SELLER") {
                    clientRoutes(clientService)
                }
            }

            get(Uris.HOME) {
                call.respondText("Hello, world!")
            }
        }
    }
}
