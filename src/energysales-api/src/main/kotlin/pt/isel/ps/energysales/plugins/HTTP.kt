package pt.isel.ps.energysales.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.users.http.model.UserProblem

interface Problem {
    val title: String
    val type: String
    val instance: String
    val status: HttpStatusCode
    val detail: String?
}

@Serializable
data class ProblemJSON(
    val title: String,
    val type: String,
    val instance: String,
    val detail: String?,
)

/**
 * Responds with a problem+json in the response body
 * @param problem The problem to be sent in the response body
 */
suspend fun ApplicationCall.respondProblem(problem: Problem) {
    response.header("Content-Type", ContentType.Application.ProblemJson.toString())
    response.status(problem.status)
    respond(ProblemJSON(problem.title, problem.type, problem.instance, problem.detail))
}

fun Application.configureHTTP() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    install(StatusPages) {
        /*  todo remove comments
            exception<Throwable> { call, _ ->
                 call.respondProblem(Problem.internalServerError, HttpStatusCode.InternalServerError)
             }*/
        exception<AuthenticationException> { call, _ ->
            call.respondProblem(UserProblem.unauthorized)
            // call.respond(status = HttpStatusCode.Unauthorized, message = cause.message ?: "Authentication failed!")
        }
        exception<BadRequestException> { call, _ ->
            call.respondProblem(UserProblem.badRequest)
            // call.respond(status = HttpStatusCode.BadRequest, message = cause.message ?: "Bad request!")
        }
        exception<NumberFormatException> { call, _ ->
            call.respondProblem(UserProblem.badRequest)
            // call.respond(status = HttpStatusCode.BadRequest, message = cause.message ?: "Bad request!")
        }
        exception<IllegalArgumentException> { call, e ->
            if (e.message == "Invalid role") {
                call.respondProblem(UserProblem.userRoleIsInvalid)
            } else {
                call.respondProblem(UserProblem.badRequest)
            }
        }
    }

    install(CORS) {
        anyHost() // This allows any host to access your API, which is fine for development but should be restricted in production.
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowSameOrigin = true
        // allow all methods
        HttpMethod.DefaultMethods.forEach {
            allowMethod(it)
        }
        allowHeaders { true } // Allow all headers
    }
}
