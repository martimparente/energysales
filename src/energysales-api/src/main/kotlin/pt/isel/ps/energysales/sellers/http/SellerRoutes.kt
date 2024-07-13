package pt.isel.ps.energysales.sellers.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.sellers.application.SellerCreationError
import pt.isel.ps.energysales.sellers.application.SellerDeletingError
import pt.isel.ps.energysales.sellers.application.SellerService
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerInput
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellerInput
import pt.isel.ps.energysales.sellers.http.model.CreateSellerRequest
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem

@Resource(Uris.SELLERS)
class SellerResource(
    val lastKeySeen: Int? = null,
    val noTeam: Boolean = false,
    val searchQuery: String? = null
) {
    @Resource("{id}")
    class Id(
        val parent: SellerResource = SellerResource(),
        val id: Int,
    )
}

fun Route.sellerRoutes(sellerService: SellerService) {
    get<SellerResource> { queryParams ->
        val input = GetAllSellerInput(queryParams.lastKeySeen, queryParams.noTeam, queryParams.searchQuery)

        val res = sellerService.getAllSellers(input)

        when (res) {
            is Right -> {
                val sellers = res.value.map { SellerJSON.fromSeller(it) }
                call.respond(sellers)
            }

            is Left -> call.respondProblem(Problem.userNotFound, HttpStatusCode.NotFound)
        }
    }

    post<SellerResource> {
        // Receive the request body
        val body = call.receive<CreateSellerRequest>()
        // Create the DTO object
        val input = CreateSellerInput(body.name, body.surname, body.email, body.team)
        // Call the service
        val res = sellerService.createSeller(input)
        // Handle the result and respond accordingly
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.SELLERS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    SellerCreationError.SellerAlreadyExists -> TODO()
                    SellerCreationError.SellerInfoIsInvalid -> TODO()
                }
        }
    }

    get<SellerResource.Id> { pathParams ->
        val sellerId = pathParams.id
        val seller =
            sellerService.getById(sellerId)
                ?: return@get call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
        val sellerJson = SellerJSON.fromSeller(seller)
        call.respond(sellerJson)
    }

    /*
    todo update
    put<SellerResource.Id> { pathParams ->
        val body = call.receive<UpdateSellerRequest>()
        if (body.uid.toInt() != pathParams.id) {
            call.respondProblem(Problem.sellerInfoIsInvalid, HttpStatusCode.BadRequest)
            return@put
        }
        val updatedSeller = Seller(pathParams.id, body.totalSales, body.team)
        val res = sellerService.updateSeller(updatedSeller)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    SellerUpdatingError.SellerNotFound -> call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
                    SellerUpdatingError.SellerInfoIsInvalid ->
                        call.respondProblem(
                            Problem.sellerInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
            }
        }
    }*/

    delete<SellerResource.Id> { pathParams ->
        val res = sellerService.deleteSeller(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    SellerDeletingError.SellerNotFound -> call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
                    SellerDeletingError.SellerInfoIsInvalid ->
                        call.respondProblem(
                            Problem.sellerInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
        }
    }
}
