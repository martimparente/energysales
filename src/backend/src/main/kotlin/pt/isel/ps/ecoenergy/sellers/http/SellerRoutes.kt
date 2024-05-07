package pt.isel.ps.ecoenergy.sellers.http

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
import pt.isel.ps.ecoenergy.Uris
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.respondProblem
import pt.isel.ps.ecoenergy.sellers.domain.model.Person
import pt.isel.ps.ecoenergy.sellers.domain.model.Seller
import pt.isel.ps.ecoenergy.sellers.domain.service.SellerCreationError
import pt.isel.ps.ecoenergy.sellers.domain.service.SellerDeletingError
import pt.isel.ps.ecoenergy.sellers.domain.service.SellerService
import pt.isel.ps.ecoenergy.sellers.domain.service.SellerUpdatingError
import pt.isel.ps.ecoenergy.sellers.http.model.CreateSellerRequest
import pt.isel.ps.ecoenergy.sellers.http.model.SellerJSON
import pt.isel.ps.ecoenergy.sellers.http.model.UpdateSellerRequest

@Resource(Uris.SELLERS)
class SellerResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: SellerResource = SellerResource(),
        val id: Int,
    )
}

fun Route.sellerRoutes(sellerService: SellerService) {
    get<SellerResource> { queryParams ->
        val sellers = sellerService.getAllSellersPaging(10, queryParams.lastKeySeen)
        val sellersResponse = sellers.map { seller -> SellerJSON.fromSeller(seller) }
        call.respond(sellersResponse)
    }

    post<SellerResource> {
        val body = call.receive<CreateSellerRequest>()

        val res = sellerService.createSeller(body.name, body.surname, body.email)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.SELLERS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    SellerCreationError.SellerAlreadyExists -> call.respondProblem(Problem.sellerEmailAlreadyInUse, HttpStatusCode.Conflict)
                    SellerCreationError.SellerInfoIsInvalid -> call.respondProblem(Problem.sellerInfoIsInvalid, HttpStatusCode.BadRequest)
                    SellerCreationError.SellerEmailIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    SellerCreationError.SellerNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    SellerCreationError.SellerSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
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
        val updatedSeller =
            Seller(
                Person(pathParams.id, body.name, body.surname, body.email),
                totalSales = body.totalSales,
                team = body.team,
            )
        val res = sellerService.updateSeller(updatedSeller)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    SellerUpdatingError.SellerNotFound -> call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
                    SellerUpdatingError.SellerInfoIsInvalid -> call.respondProblem(Problem.sellerInfoIsInvalid, HttpStatusCode.BadRequest)
                    SellerUpdatingError.SellerEmailIsInvalid -> TODO()
                    SellerUpdatingError.SellerNameIsInvalid -> TODO()
                    SellerUpdatingError.SellerSurnameIsInvalid -> TODO()
                }
            }
        }
    }

    delete<SellerResource.Id> { pathParams ->
        val res = sellerService.deleteSeller(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.NoContent)
            is Left ->
                when (res.value) {
                    SellerDeletingError.SellerNotFound -> call.respondProblem(Problem.sellerNotFound, HttpStatusCode.NotFound)
                    SellerDeletingError.SellerInfoIsInvalid -> call.respondProblem(Problem.sellerInfoIsInvalid, HttpStatusCode.BadRequest)
                }
        }
    }
}
