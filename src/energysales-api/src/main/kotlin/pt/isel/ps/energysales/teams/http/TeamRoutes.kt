package pt.isel.ps.energysales.teams.http

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
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.application.TeamAddClientError
import pt.isel.ps.energysales.teams.application.TeamAddServiceError
import pt.isel.ps.energysales.teams.application.TeamCreationError
import pt.isel.ps.energysales.teams.application.TeamDeleteServiceError
import pt.isel.ps.energysales.teams.application.TeamDeletingError
import pt.isel.ps.energysales.teams.application.TeamSellersReadingError
import pt.isel.ps.energysales.teams.application.TeamService
import pt.isel.ps.energysales.teams.application.TeamUpdateAvatarError
import pt.isel.ps.energysales.teams.application.TeamUpdatingError
import pt.isel.ps.energysales.teams.application.dto.CreateTeamInput
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamInput
import pt.isel.ps.energysales.teams.http.model.AddClientToTeamRequest
import pt.isel.ps.energysales.teams.http.model.AddServiceToTeamRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamToSellerRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.PatchTeamRequest
import pt.isel.ps.energysales.teams.http.model.TeamDetailsJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem
import java.io.File

@Resource(Uris.TEAMS)
class TeamResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{teamId}")
    class TeamId(
        val parent: TeamResource = TeamResource(),
        val teamId: Int,
        val include: String? = null,
    ) {
        @Resource(Uris.SELLERS)
        class Sellers(
            val parent: TeamId,
        ) {
            @Resource("{sellerId}")
            class SellerId(
                val parent: Sellers,
                val sellerId: Int,
            )
        }

        @Resource(Uris.SERVICES)
        class Services(
            val parent: TeamId,
        ) {
            @Resource("{serviceId}")
            class ServiceId(
                val parent: Services,
                val serviceId: Int,
            )
        }

        @Resource(Uris.CLIENTS)
        class Clients(
            val parent: TeamId,
        ) {
            @Resource("{clientId}")
            class ClientId(
                val parent: Clients,
                val clientId: Int,
            )
        }

        @Resource(Uris.AVATAR)
        class Avatar(
            val parent: TeamId,
        )
    }
}

fun Route.teamRoutes(teamService: TeamService) {
    get<TeamResource> { queryParams ->
        val teams = teamService.getAllTeamsPaging(10, queryParams.lastKeySeen)
        val teamsResponse = teams.map { team -> TeamJSON.fromTeam(team) }
        call.respond(teamsResponse)
    }

    post<TeamResource> {
        val body = call.receive<CreateTeamRequest>()
        val input =
            CreateTeamInput(
                name = body.name,
                district = body.location.district,
                managerId = body.managerId,
            )

        val res = teamService.createTeam(input)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.TEAMS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    TeamCreationError.TeamAlreadyExists -> call.respondProblem(Problem.teamAlreadyExists, HttpStatusCode.Conflict)
                    TeamCreationError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                }
        }
    }

    get<TeamResource.TeamId> { pathParams ->
        val res =
            if (pathParams.include == "details") {
                val res =
                    teamService.getByIdWithDetails(pathParams.teamId)
                        ?: return@get call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                val teamDetailsJson = TeamDetailsJSON.fromTeamDetails(res)
                call.response.status(HttpStatusCode.OK)
                call.respond(teamDetailsJson)
            } else {
                val res =
                    teamService.getById(pathParams.teamId)
                        ?: return@get call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                val teamJson = TeamJSON.fromTeam(res)
                call.response.status(HttpStatusCode.OK)
                call.respond(teamJson)
            }
    }

    patch<TeamResource.TeamId> { pathParams ->
        val body = call.receive<PatchTeamRequest>()
        val input =
            UpdateTeamInput(
                id = pathParams.teamId,
                name = body.name,
                district = body.location?.district,
                managerId = body.managerId?.toInt(),
            )

        val res = teamService.updateTeam(input)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    TeamUpdatingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamUpdatingError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                }
            }
        }
    }

    delete<TeamResource.TeamId> { pathParams ->
        val res = teamService.deleteTeam(pathParams.teamId)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    TeamDeletingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamDeletingError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                }
        }
    }

    get<TeamResource.TeamId.Sellers> { pathParams ->
        val res = teamService.getTeamSellers(pathParams.parent.teamId)
        when (res) {
            is Right -> {
                val sellersJson = res.value.map { seller -> SellerJSON.fromSeller(seller) }
                call.respond(sellersJson)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamSellersReadingError.SellerNotFound -> TODO()
                }
        }
    }

    put<TeamResource.TeamId.Sellers> { pathParams ->
        val teamId = pathParams.parent.teamId
        val body = call.receive<AddTeamToSellerRequest>()
        val sellerId =
            body.sellerId.toIntOrNull()
                ?: return@put call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest) // todo error message
        print("sellerId: $sellerId")
        // Check if the teamId in the request body = teamId in the path
        if (teamId != body.teamId.toInt()) {
            call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest) // todo error message
        }

        val res = teamService.addSellerToTeam(teamId, sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamSellersReadingError.SellerNotFound -> TODO()
                }
        }
    }

    delete<TeamResource.TeamId.Sellers.SellerId> { pathParams ->
        val sellerId = pathParams.sellerId
        val res = teamService.deleteSellerFromTeam(sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamSellersReadingError.SellerNotFound -> TODO()
                }
        }
    }

    put<TeamResource.TeamId.Services> { pathParams ->
        val body = call.receive<AddServiceToTeamRequest>()
        val teamId = pathParams.parent.teamId
        val serviceId =
            body.serviceId.toIntOrNull()
                ?: return@put call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest) // todo error message

        // todo Check if the teamId in the request body = teamId in the path

        val res = teamService.addServiceToTeam(teamId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    TeamAddServiceError.ServiceNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        ) // todo error message
                    TeamAddServiceError.TeamNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        ) // todo error message
                }
            }
        }
    }

    delete<TeamResource.TeamId.Services.ServiceId> { pathParams ->
        val serviceId = pathParams.serviceId
        val teamId = pathParams.parent.parent.teamId
        val res = teamService.deleteServiceFromTeam(teamId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    TeamDeleteServiceError.ServiceNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        ) // todo error message
                    TeamDeleteServiceError.TeamNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        ) // todo error message
                }
            }
        }
    }

    put<TeamResource.TeamId.Clients> { pathParams ->
        val body = call.receive<AddClientToTeamRequest>()
        val teamId = pathParams.parent.teamId
        val clientId =
            body.clientId.toIntOrNull()
                ?: return@put call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest) // todo error message

        // todo Check if the teamId in the request body = teamId in the path

        val res = teamService.addClientToTeam(teamId, clientId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    TeamAddClientError.SellerNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        )

                    TeamAddClientError.TeamNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        )
                }
            }
        }
    }

    post<TeamResource.TeamId.Avatar> { pathParams ->
        val multipart = call.receiveMultipart()
        var teamId = pathParams.parent.teamId
        var file: File? = null
        lateinit var pathName: String
        lateinit var fileType: String

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "teamId") {
                        teamId = part.value.toInt()
                    }
                }

                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    fileType = part.contentType?.contentSubtype.toString()

                    // Ensure directory exists
                    val directory = File("files/avatar/team")
                    pathName = "$directory/$teamId.$fileType"
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

        if (file == null) {
            call.respond(HttpStatusCode.BadRequest, "File upload failed")
            return@post
        }

        // convert pathName to url

        // Convert local path to URI
        val avatarUri = "/avatar/team/$teamId.$fileType"

        val res = teamService.addAvatar(teamId, avatarUri)

        when (res) {
            is Right -> {
                call.respond(HttpStatusCode.OK, mapOf("avatarPath" to res.value.avatarPath))
            }

            is Left -> {
                when (res.value) {
                    TeamUpdateAvatarError.AvatarImgNotFound ->
                        call.respondProblem(
                            Problem.todo,
                            HttpStatusCode.NotFound,
                        )

                    TeamUpdateAvatarError.TeamNotFound ->
                        call.respondProblem(
                            Problem.teamNotFound,
                            HttpStatusCode.NotFound,
                        )
                }
            }
        }
    }
}
