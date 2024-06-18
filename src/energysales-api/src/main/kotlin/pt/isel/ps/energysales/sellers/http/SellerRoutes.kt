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
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.sellers.domain.model.Seller
import pt.isel.ps.energysales.sellers.domain.service.SellerCreationError
import pt.isel.ps.energysales.sellers.domain.service.SellerDeletingError
import pt.isel.ps.energysales.sellers.domain.service.SellerService
import pt.isel.ps.energysales.sellers.domain.service.SellerUpdatingError
import pt.isel.ps.energysales.sellers.http.model.CreateSellerRequest
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.sellers.http.model.UpdateSellerRequest

@Resource(Uris.SELLERS)
class SellerResource(
    val lastKeySeen: Int? = null,
    val noTeam: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        val parent: SellerResource = SellerResource(),
        val id: Int,
    )
}

fun Route.sellerRoutes(sellerService: SellerService) {
    get<SellerResource> { queryParams ->
        val sellers = sellerService.getAllSellersPaging(10, queryParams.lastKeySeen, queryParams.noTeam)
        val sellersResponse = sellers.map { seller -> SellerJSON.fromSeller(seller) }
        call.respond(sellersResponse)
    }

    post<SellerResource> {
        val body = call.receive<CreateSellerRequest>()

        val res = sellerService.createSeller(body.uid, body.team)
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
        val seller =
            sellerService.getById(pathParams.id)
                ?: return@get call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
        val sellerJson = SellerJSON.fromSeller(seller)
        call.response.status(HttpStatusCode.OK)
        call.respond(sellerJson)
    }

    put<SellerResource.Id> { pathParams ->
        val body = call.receive<UpdateSellerRequest>()
        if (body.uid.toInt() != pathParams.id) {
            call.respondProblem(Problem.sellerInfoIsInvalid, HttpStatusCode.BadRequest)
            return@put
        }
        val updatedSeller = Seller(body.uid.toInt(), body.totalSales, body.team)

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
    }

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
