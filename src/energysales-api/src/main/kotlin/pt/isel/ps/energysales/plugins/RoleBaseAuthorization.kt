package pt.isel.ps.energysales.plugins

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import pt.isel.ps.energysales.users.http.model.UserProblem

class PluginConfiguration {
    var roles: Set<String> = emptySet()
}

val RoleBasedAuthorizationPlugin =
    createRouteScopedPlugin(
        name = "RbacPlugin",
        createConfiguration = ::PluginConfiguration,
    ) {
        val roles = pluginConfig.roles

        pluginConfig.apply {
            on(AuthenticationChecked) { call ->
                val tokenRole = getRoleFromToken(call) ?: ""

                val authorized = tokenRole in roles

                if (!authorized) {
                    println("User does not have permission to access this resource.")
                    call.respond(call.respondProblem(UserProblem.forbidden))
                }
            }
        }
    }

class AuthorizedRouteSelector(
    private val description: String,
) : RouteSelector() {
    override fun evaluate(
        context: RoutingResolveContext,
        segmentIndex: Int,
    ) = RouteSelectorEvaluation.Constant

    override fun toString(): String = "(authorize $description)"
}

private fun getRoleFromToken(call: ApplicationCall): String? =
    call
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("role")
        ?.asString()

fun Route.authorize(
    vararg hasAnyRole: String,
    build: Route.() -> Unit,
) {
    val description = hasAnyRole.joinToString(",")
    val authorizedRoute = createChild(AuthorizedRouteSelector(description))
    authorizedRoute.install(RoleBasedAuthorizationPlugin) { roles = hasAnyRole.toSet() }
    authorizedRoute.build()
}
