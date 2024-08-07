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
import pt.isel.ps.energysales.plugins.authorize
import pt.isel.ps.energysales.plugins.respondProblem
import pt.isel.ps.energysales.sellers.application.SellerServiceKtor
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerError
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerInput
import pt.isel.ps.energysales.sellers.application.dto.DeleteSellerError
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellerInput
import pt.isel.ps.energysales.sellers.http.model.CreateSellerRequest
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.sellers.http.model.SellerProblem

@Resource(Uris.SELLERS)
class SellerResource(
    val lastKeySeen: String? = null,
    val noPartner: Boolean = false,
    val searchQuery: String? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: SellerResource = SellerResource(),
        val id: String,
    )
}

fun Route.sellerRoutes(sellerService: SellerServiceKtor) {
    authorize("ADMIN") {
        get<SellerResource> { queryParams ->
            val input = GetAllSellerInput(queryParams.lastKeySeen, queryParams.noPartner, queryParams.searchQuery)
            val res = sellerService.getAllSellers(input)

            when (res) {
                is Right -> {
                    val sellers = res.value.map { SellerJSON.fromSeller(it) }
                    call.respond(sellers)
                }

                is Left -> call.respondProblem(SellerProblem.sellerNotFound)
            }
        }
    }

    post<SellerResource> {
        val body = call.receive<CreateSellerRequest>()
        val input = CreateSellerInput(body.name, body.surname, body.email, body.partner)
        val res = sellerService.createSeller(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.SELLERS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    CreateSellerError.SellerAlreadyExists -> call.respondProblem(SellerProblem.sellerAlreadyExists)
                    CreateSellerError.SellerInfoIsInvalid -> call.respondProblem(SellerProblem.sellerIsInvalid)
                }
        }
    }

    get<SellerResource.Id> { params ->
        val seller = sellerService.getById(params.id) ?: return@get call.respondProblem(SellerProblem.sellerNotFound)
        call.respond(SellerJSON.fromSeller(seller))
    }

    delete<SellerResource.Id> { params ->
        val res = sellerService.deleteSeller(params.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    DeleteSellerError.SellerNotFound -> call.respondProblem(SellerProblem.sellerNotFound)
                    DeleteSellerError.SellerInfoIsInvalid -> call.respondProblem(SellerProblem.sellerIsInvalid)
                }
        }
    }
}
