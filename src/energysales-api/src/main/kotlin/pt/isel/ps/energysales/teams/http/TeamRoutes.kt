package pt.isel.ps.energysales.teams.http

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
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.application.TeamService
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
import pt.isel.ps.energysales.teams.http.model.AddClientToTeamRequest
import pt.isel.ps.energysales.teams.http.model.AddServiceToTeamRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamToSellerRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.TeamDetailsJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.teams.http.model.UpdateTeamRequest
import pt.isel.ps.energysales.users.http.model.Problem
import pt.isel.ps.energysales.users.http.model.respondProblem

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
                    CreateTeamError.TeamAlreadyExists -> call.respondProblem(Problem.teamAlreadyExists, HttpStatusCode.Conflict)
                    CreateTeamError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                }
        }
    }

    get<TeamResource.TeamId> { pathParams ->
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

    put<TeamResource.TeamId> { pathParams ->
        val body = call.receive<UpdateTeamRequest>()
        val input = UpdateTeamInput(pathParams.teamId, body.name, body.location.toLocation(), body.managerId)

        val res = teamService.updateTeam(input)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    UpdateTeamError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                    UpdateTeamError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
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
                    DeleteTeamError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                    DeleteTeamError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
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
                    GetTeamSellersError.SellerNotFound -> TODO()
                    GetTeamSellersError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }

    put<TeamResource.TeamId.Sellers> { pathParams ->
        val teamId = pathParams.parent.teamId
        // todo remove teamId from the request body
        val body = call.receive<AddTeamToSellerRequest>()
        val sellerId = body.sellerId

        // Check if the teamId in the request body = teamId in the path
        if (teamId != body.teamId) {
            call.respondProblem(Problem.badRequest, HttpStatusCode.BadRequest) // todo error message
        }

        val res = teamService.addTeamSeller(teamId, sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    AddTeamSellerError.SellerNotFound -> TODO()
                    AddTeamSellerError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }

    delete<TeamResource.TeamId.Sellers.SellerId> { pathParams ->
        val sellerId = pathParams.sellerId
        val res = teamService.deleteTeamSeller(sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    DeleteTeamSellerError.SellerNotFound -> TODO()
                    DeleteTeamSellerError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }

    put<TeamResource.TeamId.Services> { pathParams ->
        val body = call.receive<AddServiceToTeamRequest>()
        val teamId = pathParams.parent.teamId
        val serviceId = body.serviceId

        val res = teamService.addTeamService(teamId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddTeamServiceError.ServiceNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    AddTeamServiceError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
            }
        }
    }

    delete<TeamResource.TeamId.Services.ServiceId> { pathParams ->
        val serviceId = pathParams.serviceId
        val teamId = pathParams.parent.parent.teamId
        val res = teamService.deleteTeamService(teamId, serviceId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    DeleteTeamServiceError.ServiceNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    DeleteTeamServiceError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
            }
        }
    }

    put<TeamResource.TeamId.Clients> { pathParams ->
        val body = call.receive<AddClientToTeamRequest>()
        val teamId = pathParams.parent.teamId
        val clientId = body.clientId

        // todo remove teamId from the request body

        val res = teamService.addTeamClient(teamId, clientId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    AddTeamClientError.SellerNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    AddTeamClientError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
            }
        }
    }
}
