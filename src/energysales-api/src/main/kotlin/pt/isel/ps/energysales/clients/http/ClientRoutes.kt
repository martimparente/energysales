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
import pt.isel.ps.energysales.clients.application.ClientServiceKtor
import pt.isel.ps.energysales.clients.application.OfferServiceKtor
import pt.isel.ps.energysales.clients.application.dto.CreateClientError
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.CreateOfferError
import pt.isel.ps.energysales.clients.application.dto.CreateOfferInput
import pt.isel.ps.energysales.clients.application.dto.DeleteClientError
import pt.isel.ps.energysales.clients.application.dto.UpdateClientError
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.http.model.ClientJSON
import pt.isel.ps.energysales.clients.http.model.CreateClientRequest
import pt.isel.ps.energysales.clients.http.model.CreateOfferRequest
import pt.isel.ps.energysales.clients.http.model.OfferLinkJSON
import pt.isel.ps.energysales.clients.http.model.UpdateClientRequest
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem

@Resource(Uris.CLIENTS)
class ClientResource(
    val lastKeySeen: String? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: ClientResource = ClientResource(),
        val id: String,
    ) {
        @Resource(Uris.OFFERS)
        class OfferResource(
            val parent: Id,
        ) {
            @Resource("/email")
            class EmailResource(
                val parent: OfferResource,
            )
        }
    }
}

fun Route.clientRoutes(
    clientService: ClientServiceKtor,
    offerService: OfferServiceKtor,
) {
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
                body.email,
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
                    CreateClientError.ClientAlreadyExists ->
                        call.respondProblem(
                            Problem.clientEmailAlreadyInUse,
                            HttpStatusCode.Conflict,
                        )

                    CreateClientError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    CreateClientError.ClientEmailIsInvalid -> call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest)
                    CreateClientError.ClientNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    CreateClientError.ClientSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                }
        }
    }

    get<ClientResource.Id> { params ->
        val client =
            clientService.getById(params.id)
                ?: return@get call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
        val clientJson = ClientJSON.fromClient(client)
        call.respond(clientJson)
    }

    put<ClientResource.Id> { params ->
        val userId =
            call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                ?: return@put call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
        val body = call.receive<UpdateClientRequest>()
        val input =
            UpdateClientInput(
                params.id,
                body.name,
                body.nif,
                body.phone,
                body.email,
                body.location.toLocation(),
                userId,
            )
        val res = clientService.updateClient(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    UpdateClientError.ClientNotFound -> call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
                    UpdateClientError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    UpdateClientError.ClientEmailIsInvalid ->
                        call.respondProblem(
                            Problem.badRequest,
                            HttpStatusCode.BadRequest,
                        )

                    UpdateClientError.ClientNameIsInvalid -> TODO()
                    UpdateClientError.ClientSurnameIsInvalid -> TODO()
                }
            }
        }
    }

    delete<ClientResource.Id> { params ->
        val res = clientService.deleteClient(params.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    DeleteClientError.ClientNotFound -> call.respondProblem(Problem.clientNotFound, HttpStatusCode.NotFound)
                    DeleteClientError.ClientInfoIsInvalid ->
                        call.respondProblem(
                            Problem.clientInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
        }
    }

    get<ClientResource.Id.OfferResource> { params ->
        /* val offers = clientService.getClientOffers(params.parent.id)
         val offersResponse = offers.map { offer -> OfferJSON.fromOffer(offer) }
         call.respond(offersResponse)*/
    }

    post<ClientResource.Id.OfferResource> {
        val userId =
            call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                ?: return@post call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
        val body = call.receive<CreateOfferRequest>()
        val input =
            CreateOfferInput(
                body.clientId,
                body.serviceId,
                body.dueInDays,
                userId,
            )
        val res = offerService.createOffer(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                val offerLinkJson = OfferLinkJSON(res.value.url, res.value.dueDate)
                call.respond(offerLinkJson)
            }

            is Left ->
                when (res.value) {
                    CreateOfferError.OfferAlreadyExists -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
                    CreateOfferError.OfferEmailIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
                    CreateOfferError.OfferInfoIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
                    CreateOfferError.OfferNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
                    CreateOfferError.OfferSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
                }
        }
    }
    post<ClientResource.Id.OfferResource.EmailResource> { params ->
        // this route sends an email to the client with the offer
        val userId =
            call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                ?: return@post call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
        val clientId = params.parent.parent.id

        val res = offerService.sendOfferByEmail(clientId)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left -> call.respondProblem(Problem.todo, HttpStatusCode.BadRequest)
        }
    }
}
