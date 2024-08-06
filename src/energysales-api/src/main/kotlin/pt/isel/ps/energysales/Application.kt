package pt.isel.ps.energysales

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.event.Level
import pt.isel.ps.energysales.clients.application.ClientServiceKtor
import pt.isel.ps.energysales.clients.application.OfferServiceKtor
import pt.isel.ps.energysales.clients.data.PsqlClientRepository
import pt.isel.ps.energysales.clients.data.PsqlOfferRepository
import pt.isel.ps.energysales.clients.http.clientRoutes
import pt.isel.ps.energysales.email.SimpleJavaMailService
import pt.isel.ps.energysales.email.model.MailConfig
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.plugins.configureAuth
import pt.isel.ps.energysales.plugins.configureDatabases
import pt.isel.ps.energysales.plugins.configureHTTP
import pt.isel.ps.energysales.plugins.configureSerialization
import pt.isel.ps.energysales.sellers.application.SellerServiceKtor
import pt.isel.ps.energysales.sellers.data.PsqlSellerRepository
import pt.isel.ps.energysales.sellers.http.sellerRoutes
import pt.isel.ps.energysales.services.application.ServiceServiceKtor
import pt.isel.ps.energysales.services.data.PsqlServiceRepository
import pt.isel.ps.energysales.services.http.serviceRoutes
import pt.isel.ps.energysales.teams.application.TeamServiceKtor
import pt.isel.ps.energysales.teams.data.PsqlTeamRepository
import pt.isel.ps.energysales.teams.http.teamRoutes
import pt.isel.ps.energysales.users.application.UserServiceKtor
import pt.isel.ps.energysales.users.application.security.HashingServiceSHA256
import pt.isel.ps.energysales.users.application.security.JwtConfig
import pt.isel.ps.energysales.users.application.security.TokenServiceJwt
import pt.isel.ps.energysales.users.data.UserRepositoryPsql
import pt.isel.ps.energysales.users.http.authRoutes
import pt.isel.ps.energysales.users.http.userRoutes
import java.io.File

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
            configProperty("jwt.expiresIn").toInt(),
        )

    val mailer =
        MailerBuilder
            .withSMTPServer(
                configProperty("email.smtp.host"),
                configProperty("email.smtp.port").toInt(),
                configProperty("email.smtp.user"),
                configProperty("email.smtp.password"),
            ).buildMailer()

    val mailConfig =
        MailConfig(
            configProperty("email.fromUsername"),
            configProperty("email.fromEmail"),
            configProperty("email.resetLinkBaseUrl"),
        )

    /**
     * Services
     */
    val mailService by lazy {
        SimpleJavaMailService(
            mailer = mailer,
            config = mailConfig,
        )
    }

    val userService by lazy {
        UserServiceKtor(
            userRepository = UserRepositoryPsql(),
            tokenService = TokenServiceJwt(jwtConfig),
            hashingService = HashingServiceSHA256(),
            mailService = mailService,
        )
    }

    val teamService by lazy {
        TeamServiceKtor(
            teamRepository = PsqlTeamRepository(),
            sellerRepository = PsqlSellerRepository(),
        )
    }

    val sellerService by lazy { SellerServiceKtor(sellerRepository = PsqlSellerRepository()) }

    val productService by lazy { ServiceServiceKtor(serviceRepository = PsqlServiceRepository()) }

    val clientService by lazy { ClientServiceKtor(clientRepository = PsqlClientRepository()) }

    val offerService by lazy {
        OfferServiceKtor(
            offerRepository = PsqlOfferRepository(),
            sellerRepository = PsqlSellerRepository(),
            clientRepository = PsqlClientRepository(),
            serviceRepository = PsqlServiceRepository(),
            mailService = mailService,
        )
    }

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
        // Static resources for the avatar images
        staticFiles("/", File("files"))

        singlePageApplication {
            react("./app/energysales-spa/dist")
        }

        route(Uris.API) {
            authRoutes(userService)

            authenticate {
                authorize("ADMIN") {
                    userRoutes(userService)
                    teamRoutes(teamService)
                    serviceRoutes(productService)
                }
                authorize("MANAGER") {
                    sellerRoutes(sellerService)
                }

                authorize("MANAGER", "SELLER") {
                    clientRoutes(clientService, offerService)
                }
            }
        }
    }
}
