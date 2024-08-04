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
import pt.isel.ps.energysales.plugins.respondProblem
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.application.TeamService
import pt.isel.ps.energysales.teams.application.dto.AddTeamAvatarError
import pt.isel.ps.energysales.teams.application.dto.AddTeamClientError
import pt.isel.ps.energysales.teams.application.dto.AddTeamSellerError
import pt.isel.ps.energysales.teams.application.dto.AddTeamServiceError
import pt.isel.ps.energysales.teams.application.dto.CreateTeamError
import pt.isel.ps.energysales.teams.application.dto.CreateTeamInput
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamError
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamSellerError
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamServiceError
import pt.isel.ps.energysales.teams.application.dto.GetTeamSellersError
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamError
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamInput
import pt.isel.ps.energysales.teams.http.model.AddTeamClientRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamSellerRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamServiceRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.PatchTeamRequest
import pt.isel.ps.energysales.teams.http.model.TeamDetailsJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.teams.http.model.TeamProblem
import java.io.File

@Resource(Uris.TEAMS)
class TeamResource(
    val lastKeySeen: String? = null,
) {
    @Resource("{teamId}")
    class TeamId(
        val parent: TeamResource = TeamResource(),
        val teamId: String,
        val include: String? = null,
    ) {
        @Resource(Uris.SELLERS)
        class Sellers(
            val parent: TeamId,
        ) {
            @Resource("{sellerId}")
            class SellerId(
                val parent: Sellers,
                val sellerId: String,
            )
        }

        @Resource(Uris.SERVICES)
        class Services(
            val parent: TeamId,
        ) {
            @Resource("{serviceId}")
            class ServiceId(
                val parent: Services,
                val serviceId: String,
            )
        }

        @Resource(Uris.CLIENTS)
        class Clients(
            val parent: TeamId,
        ) {
            @Resource("{clientId}")
            class ClientId(
                val parent: Clients,
                val clientId: String,
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
        val input = CreateTeamInput(body.name, body.location.toLocation(), body.managerId)
        val res = teamService.createTeam(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.TEAMS}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    CreateTeamError.TeamAlreadyExists -> call.respondProblem(TeamProblem.teamAlreadyExists)
                    CreateTeamError.TeamInfoIsInvalid -> call.respondProblem(TeamProblem.teamInfoIsInvalid)
                }
        }
    }

    get<TeamResource.TeamId> { params ->
        if (params.include == "details") {
            val res =
                teamService.getByIdWithDetails(params.teamId)
                    ?: return@get call.respondProblem(TeamProblem.teamNotFound)
            val teamDetailsJson = TeamDetailsJSON.fromTeamDetails(res)
            call.respond(teamDetailsJson)
        } else {
            val res =
                teamService.getById(params.teamId)
                    ?: return@get call.respondProblem(TeamProblem.teamNotFound)
            val teamJson = TeamJSON.fromTeam(res)
            call.respond(teamJson)
        }
    }

    patch<TeamResource.TeamId> { params ->
        val body = call.receive<PatchTeamRequest>()
        val input = UpdateTeamInput(params.teamId, body.name, body.location?.toLocation(), body.managerId)
        val res = teamService.updateTeam(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    UpdateTeamError.TeamInfoIsInvalid -> call.respondProblem(TeamProblem.teamInfoIsInvalid)
                    UpdateTeamError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
            }
        }
    }

    delete<TeamResource.TeamId> { params ->
        val res = teamService.deleteTeam(params.teamId)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    DeleteTeamError.TeamInfoIsInvalid -> call.respondProblem(TeamProblem.teamInfoIsInvalid)
                    DeleteTeamError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
        }
    }

    get<TeamResource.TeamId.Sellers> { params ->
        val res = teamService.getTeamSellers(params.parent.teamId)

        when (res) {
            is Right -> {
                val sellersJson = res.value.map { seller -> SellerJSON.fromSeller(seller) }
                call.respond(sellersJson)
            }

            is Left ->
                when (res.value) {
                    GetTeamSellersError.SellerNotFound -> TODO()
                    GetTeamSellersError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
        }
    }

    put<TeamResource.TeamId.Sellers> { params ->
        val body = call.receive<AddTeamSellerRequest>()
        val res = teamService.addTeamSeller(params.parent.teamId, body.sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    AddTeamSellerError.SellerNotFound -> TODO()
                    AddTeamSellerError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
        }
    }

    delete<TeamResource.TeamId.Sellers.SellerId> { params ->
        val res = teamService.deleteTeamSeller(params.sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    DeleteTeamSellerError.SellerNotFound -> TODO()
                    DeleteTeamSellerError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
        }
    }

    put<TeamResource.TeamId.Services> { params ->
        val body = call.receive<AddTeamServiceRequest>()
        val res = teamService.addTeamService(params.parent.teamId, body.serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddTeamServiceError.ServiceNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                    AddTeamServiceError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
            }
        }
    }

    delete<TeamResource.TeamId.Services.ServiceId> { params ->
        val serviceId = params.serviceId
        val teamId = params.parent.parent.teamId
        val res = teamService.deleteTeamService(teamId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    DeleteTeamServiceError.ServiceNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                    DeleteTeamServiceError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
            }
        }
    }

    put<TeamResource.TeamId.Clients> { params ->
        val body = call.receive<AddTeamClientRequest>()
        val res = teamService.addTeamClient(params.parent.teamId, body.clientId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddTeamClientError.SellerNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                    AddTeamClientError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
            }
        }
    }

    post<TeamResource.TeamId.Avatar> { params ->
        val multipart = call.receiveMultipart()
        var teamId = params.parent.teamId
        var file: File? = null
        lateinit var pathName: String
        lateinit var fileType: String

        // Iterate over all the parts of the request
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "teamId") {
                        teamId = part.value
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

        // Check if file was uploaded
        if (file == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "File upload failed")
        }

        // Convert local path to URI
        val avatarUri = "/avatar/team/$teamId.$fileType"
        val res = teamService.addTeamAvatar(teamId, avatarUri)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK, mapOf("avatarPath" to res.value))

            is Left -> {
                when (res.value) {
                    AddTeamAvatarError.AvatarImgNotFound -> call.respondProblem(TeamProblem.teamNotFound)

                    AddTeamAvatarError.TeamNotFound -> call.respondProblem(TeamProblem.teamNotFound)
                }
            }
        }
    }
}
