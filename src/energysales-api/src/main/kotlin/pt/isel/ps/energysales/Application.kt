package pt.isel.ps.energysales

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.event.Level
import pt.isel.ps.energysales.clients.application.ClientService
import pt.isel.ps.energysales.clients.data.PsqlClientRepository
import pt.isel.ps.energysales.clients.http.clientRoutes
import pt.isel.ps.energysales.email.SimpleJavaEmailService
import pt.isel.ps.energysales.email.model.EmailConfig
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.plugins.configureDatabases
import pt.isel.ps.energysales.plugins.configureHTTP
import pt.isel.ps.energysales.plugins.configureSerialization
import pt.isel.ps.energysales.sellers.application.SellerService
import pt.isel.ps.energysales.sellers.data.PsqlSellerRepository
import pt.isel.ps.energysales.sellers.http.sellerRoutes
import pt.isel.ps.energysales.services.application.ServiceService
import pt.isel.ps.energysales.services.data.PsqlServiceRepository
import pt.isel.ps.energysales.services.http.serviceRoutes
import pt.isel.ps.energysales.teams.application.TeamService
import pt.isel.ps.energysales.teams.data.PsqlTeamRepository
import pt.isel.ps.energysales.teams.http.teamRoutes
import pt.isel.ps.energysales.users.application.UserService
import pt.isel.ps.energysales.users.application.security.JwtConfig
import pt.isel.ps.energysales.users.application.security.JwtTokenService
import pt.isel.ps.energysales.users.application.security.SHA256HashingService
import pt.isel.ps.energysales.users.configureAuth
import pt.isel.ps.energysales.users.data.PsqlUserRepository
import pt.isel.ps.energysales.users.http.authRoutes
import pt.isel.ps.energysales.users.http.userRoutes

fun main(args: Array<String>) = EngineMain.main(args)

// Todo check behaviour nested transactions w\ suspendTransactions

fun Application.module() {
    /**
     * Configuration
     */

    fun configProperty(propertyName: String) = environment.config.property(propertyName).getString()

    val jwtConfig =
        JwtConfig(
            configProperty("jwt.issuer"),
            configProperty("jwt.audience"),
            configProperty("jwt.realm"),
            configProperty("jwt.secret"),
        )

    val mailer =
        MailerBuilder
            .withSMTPServer(
                configProperty("email.smtp.host"),
                configProperty("email.smtp.port").toInt(),
                configProperty("email.smtp.user"),
                configProperty("email.smtp.password"),
            ).buildMailer()

    val emailConfig =
        EmailConfig(
            configProperty("email.fromUsername"),
            configProperty("email.fromEmail"),
            configProperty("email.resetLinkBaseUrl"),
        )

    /**
     * Services
     */

    val userService by lazy {
        UserService(
            userRepository = PsqlUserRepository(),
            tokenService = JwtTokenService(jwtConfig),
            hashingService = SHA256HashingService(),
            emailService =
            SimpleJavaEmailService(
                mailer = mailer,
                config = emailConfig,
            ),
        )
    }
    val teamService by lazy {
        TeamService(
            teamRepository = PsqlTeamRepository(),
            sellerRepository = PsqlSellerRepository(),
        )
    }
    val sellerService by lazy { SellerService(sellerRepository = PsqlSellerRepository()) }
    val productService by lazy { ServiceService(serviceRepository = PsqlServiceRepository()) }
    val clientService by lazy { ClientService(clientRepository = PsqlClientRepository()) }

    /**
     * Plugins
     */

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(Resources)
    configureSerialization()
    configureDatabases()
    configureAuth(jwtConfig)
    configureHTTP()

    /**
     * Routes
     */
    routing {
        singlePageApplication {
            react("./app/energysales-spa/dist")
        }

        route(Uris.API) {
            authRoutes(userService)
            userRoutes(userService)
            authenticate {
                authorize("ADMIN") {
                    teamRoutes(teamService)
                    sellerRoutes(sellerService)
                    serviceRoutes(productService)
                    clientRoutes(clientService)
                }
                authorize("SELLER") {
                }
            }
        }
    }
}
