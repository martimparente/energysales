package pt.isel.ps.energysales.clients.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.clients.domain.model.Client
import pt.isel.ps.energysales.clients.domain.service.ClientCreationError
import pt.isel.ps.energysales.clients.domain.service.ClientDeletingError
import pt.isel.ps.energysales.clients.domain.service.ClientService
import pt.isel.ps.energysales.clients.domain.service.ClientUpdatingError
import pt.isel.ps.energysales.clients.http.model.ClientJSON
import pt.isel.ps.energysales.clients.http.model.CreateClientRequest
import pt.isel.ps.energysales.clients.http.model.UpdateClientRequest
import pt.isel.ps.energysales.teams.domain.model.Location

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
        val body = call.receive<CreateClientRequest>()

        val res = clientService.createClient(body.name, body.nif, body.phone, body.district)
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
        val body = call.receive<UpdateClientRequest>()
        val updatedClient = Client(pathParams.id, body.name, body.nif, body.phone, Location(body.district))

        val res = clientService.updateClient(updatedClient)
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
