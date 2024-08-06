package pt.isel.ps.energysales.partners.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.partners.application.PartnerService
import pt.isel.ps.energysales.partners.application.dto.AddPartnerAvatarError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerClientError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerSellerError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerServiceError
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerError
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerInput
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerError
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerSellerError
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerServiceError
import pt.isel.ps.energysales.partners.application.dto.GetPartnerSellersError
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerError
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerInput
import pt.isel.ps.energysales.partners.http.model.AddPartnerClientRequest
import pt.isel.ps.energysales.partners.http.model.AddPartnerSellerRequest
import pt.isel.ps.energysales.partners.http.model.AddPartnerServiceRequest
import pt.isel.ps.energysales.partners.http.model.CreatePartnerRequest
import pt.isel.ps.energysales.partners.http.model.PartnerDetailsJSON
import pt.isel.ps.energysales.partners.http.model.PartnerJSON
import pt.isel.ps.energysales.partners.http.model.PartnerProblem
import pt.isel.ps.energysales.partners.http.model.PatchPartnerRequest
import pt.isel.ps.energysales.plugins.respondProblem
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import java.io.File

@Resource(Uris.PARTNERS)
class PartnerResource(
    val lastKeySeen: String? = null,
) {
    @Resource("{partnerId}")
    class PartnerId(
        val parent: PartnerResource = PartnerResource(),
        val partnerId: String,
        val include: String? = null,
    ) {
        @Resource(Uris.SELLERS)
        class Sellers(
            val parent: PartnerId,
        ) {
            @Resource("{sellerId}")
            class SellerId(
                val parent: Sellers,
                val sellerId: String,
            )
        }

        @Resource(Uris.SERVICES)
        class Services(
            val parent: PartnerId,
        ) {
            @Resource("{serviceId}")
            class ServiceId(
                val parent: Services,
                val serviceId: String,
            )
        }

        @Resource(Uris.CLIENTS)
        class Clients(
            val parent: PartnerId,
        ) {
            @Resource("{clientId}")
            class ClientId(
                val parent: Clients,
                val clientId: String,
            )
        }

        @Resource(Uris.AVATAR)
        class Avatar(
            val parent: PartnerId,
        )
    }
}

fun Route.partnerRoutes(partnerService: PartnerService) {
    get<PartnerResource> { queryParams ->
        val partners = partnerService.getAllPartnerPaging(10, queryParams.lastKeySeen)
        val partnersResponse = partners.map { partner -> PartnerJSON.fromPartner(partner) }
        call.respond(partnersResponse)
    }

    post<PartnerResource> {
        val body = call.receive<CreatePartnerRequest>()
        val input = CreatePartnerInput(body.name, body.location.toLocation(), body.managerId)
        val res = partnerService.createPartner(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.PARTNERS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    CreatePartnerError.PartnerAlreadyExists -> call.respondProblem(PartnerProblem.partnerAlreadyExists)
                    CreatePartnerError.PartnerInfoIsInvalid -> call.respondProblem(PartnerProblem.partnerInfoIsInvalid)
                }
        }
    }

    get<PartnerResource.PartnerId> { params ->
        if (params.include == "details") {
            val res =
                partnerService.getByIdWithDetails(params.partnerId)
                    ?: return@get call.respondProblem(PartnerProblem.partnerNotFound)
            val partnerDetailsJson = PartnerDetailsJSON.fromPartnerDetails(res)
            call.respond(partnerDetailsJson)
        } else {
            val res =
                partnerService.getById(params.partnerId)
                    ?: return@get call.respondProblem(PartnerProblem.partnerNotFound)
            val partnerJson = PartnerJSON.fromPartner(res)
            call.respond(partnerJson)
        }
    }

    patch<PartnerResource.PartnerId> { params ->
        val body = call.receive<PatchPartnerRequest>()
        val input = UpdatePartnerInput(params.partnerId, body.name, body.location?.toLocation(), body.managerId)
        val res = partnerService.updatePartner(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    UpdatePartnerError.PartnerInfoIsInvalid -> call.respondProblem(PartnerProblem.partnerInfoIsInvalid)
                    UpdatePartnerError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
            }
        }
    }

    delete<PartnerResource.PartnerId> { params ->
        val res = partnerService.deletePartner(params.partnerId)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    DeletePartnerError.PartnerInfoIsInvalid -> call.respondProblem(PartnerProblem.partnerInfoIsInvalid)
                    DeletePartnerError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
        }
    }

    get<PartnerResource.PartnerId.Sellers> { params ->
        val res = partnerService.getPartnerSellers(params.parent.partnerId)

        when (res) {
            is Right -> {
                val sellersJson = res.value.map { seller -> SellerJSON.fromSeller(seller) }
                call.respond(sellersJson)
            }

            is Left ->
                when (res.value) {
                    GetPartnerSellersError.SellerNotFound -> TODO()
                    GetPartnerSellersError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
        }
    }

    put<PartnerResource.PartnerId.Sellers> { params ->
        val body = call.receive<AddPartnerSellerRequest>()
        val res = partnerService.addPartnerSeller(params.parent.partnerId, body.sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    AddPartnerSellerError.SellerNotFound -> TODO()
                    AddPartnerSellerError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
        }
    }

    delete<PartnerResource.PartnerId.Sellers.SellerId> { params ->
        val res = partnerService.deletePartnerSeller(params.sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    DeletePartnerSellerError.SellerNotFound -> TODO()
                    DeletePartnerSellerError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
        }
    }

    put<PartnerResource.PartnerId.Services> { params ->
        val body = call.receive<AddPartnerServiceRequest>()
        val res = partnerService.addPartnerService(params.parent.partnerId, body.serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddPartnerServiceError.ServiceNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                    AddPartnerServiceError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
            }
        }
    }

    delete<PartnerResource.PartnerId.Services.ServiceId> { params ->
        val serviceId = params.serviceId
        val partnerId = params.parent.parent.partnerId
        val res = partnerService.deletePartnerService(partnerId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    DeletePartnerServiceError.ServiceNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                    DeletePartnerServiceError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
            }
        }
    }

    put<PartnerResource.PartnerId.Clients> { params ->
        val body = call.receive<AddPartnerClientRequest>()
        val res = partnerService.addPartnerClient(params.parent.partnerId, body.clientId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddPartnerClientError.SellerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                    AddPartnerClientError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
            }
        }
    }

    post<PartnerResource.PartnerId.Avatar> { params ->
        val multipart = call.receiveMultipart()
        var partnerId = params.parent.partnerId
        var file: File? = null
        lateinit var pathName: String
        lateinit var fileType: String

        // Iterate over all the parts of the request
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "partnerId") {
                        partnerId = part.value
                    }
                }

                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    fileType = part.contentType?.contentSubtype.toString()

                    // Ensure directory exists
                    val directory = File("files/avatar/partner")
                    pathName = "$directory/$partnerId.$fileType"
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }
                    file =
                        File(pathName).apply {
                            writeBytes(fileBytes)
                        }
                }

                else -> {}
            }
            part.dispose()
        }

        // Check if file was uploaded
        if (file == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "File upload failed")
        }

        // Convert local path to URI
        val avatarUri = "/avatar/partner/$partnerId.$fileType"
        val res = partnerService.addPartnerAvatar(partnerId, avatarUri)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK, mapOf("avatarPath" to res.value))

            is Left -> {
                when (res.value) {
                    AddPartnerAvatarError.AvatarImgNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)

                    AddPartnerAvatarError.PartnerNotFound -> call.respondProblem(PartnerProblem.partnerNotFound)
                }
            }
        }
    }
}
