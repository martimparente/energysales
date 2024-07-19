package pt.isel.ps.energysales.clients.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.clients.application.ClientCreationError
import pt.isel.ps.energysales.clients.application.ClientDeletingError
import pt.isel.ps.energysales.clients.application.ClientService
import pt.isel.ps.energysales.clients.application.ClientUpdatingError
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.http.model.ClientJSON
import pt.isel.ps.energysales.clients.http.model.CreateClientRequest
import pt.isel.ps.energysales.clients.http.model.UpdateClientRequest
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem

@Resource(Uris.CLIENTS)
class ClientResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: ClientResource = ClientResource(),
        val id: Int,
    )
}

fun Route.clientRoutes(clientService: ClientService) {
    get<ClientResource> { queryParams ->
        val clients = clientService.getAllClientsPaging(10, queryParams.lastKeySeen)
        val clientsResponse = clients.map { client -> ClientJSON.fromClient(client) }
        call.respond(clientsResponse)
    }

    post<ClientResource> {
        val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class) ?: "Unknown"
        val body = call.receive<CreateClientRequest>()
        val input =
            CreateClientInput(
                body.name,
                body.nif,
                body.phone,
                body.location.toLocation(),
                userId,
            )

        val res = clientService.createClient(input)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.CLIENTS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    ClientCreationError.ClientAlreadyExists ->
                        call.respondProblem(
                            Problem.clientEmailAlreadyInUse,
                            HttpStatusCode.Conflict,
                        )

                    ClientCreationError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ClientCreationError.ClientEmailIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ClientCreationError.ClientNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ClientCreationError.ClientSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                }
        }
    }

    get<ClientResource.Id> { pathParams ->
        val client =
            clientService.getById(pathParams.id)
                ?: return@get call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
        val clientJson = ClientJSON.fromClient(client)
        call.response.status(HttpStatusCode.OK)
        call.respond(clientJson)
    }

    put<ClientResource.Id> { pathParams ->
        val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
            ?: call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
        val body = call.receive<UpdateClientRequest>()
        val input =
            UpdateClientInput(
                body.id,
                body.name,
                body.nif,
                body.phone,
                body.location.toLocation(),
                userId.toString(),
            )

        val res = clientService.updateClient(input)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    ClientUpdatingError.ClientNotFound -> call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
                    ClientUpdatingError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ClientUpdatingError.ClientEmailIsInvalid -> TODO()
                    ClientUpdatingError.ClientNameIsInvalid -> TODO()
                    ClientUpdatingError.ClientSurnameIsInvalid -> TODO()
                }
            }
        }
    }

    delete<ClientResource.Id> { pathParams ->
        val res = clientService.deleteClient(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    ClientDeletingError.ClientNotFound -> call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
                    ClientDeletingError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
        }
    }
}
