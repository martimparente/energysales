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
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.domain.model.Location
import pt.isel.ps.energysales.teams.domain.model.Person
import pt.isel.ps.energysales.teams.domain.model.Team
import pt.isel.ps.energysales.teams.domain.service.TeamCreationError
import pt.isel.ps.energysales.teams.domain.service.TeamDeletingError
import pt.isel.ps.energysales.teams.domain.service.TeamSellersReadingError
import pt.isel.ps.energysales.teams.domain.service.TeamService
import pt.isel.ps.energysales.teams.domain.service.TeamUpdatingError
import pt.isel.ps.energysales.teams.http.model.AddTeamSellerRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.TeamDetailsJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.teams.http.model.UpdateTeamRequest

@Resource(Uris.TEAMS)
class TeamResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: TeamResource = TeamResource(),
        val id: Int,
        val include: String? = null,
    ) {
        @Resource(Uris.SELLERS)
        class Sellers(
            val parent: Id,
        ) {
            @Resource("{sellerId}")
            class Id(
                val parent: Sellers,
                val id: Int,
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

        val res = teamService.createTeam(body.name, body.location.district, body.manager)
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

    get<TeamResource.Id> { pathParams ->
        val res =
            if (pathParams.include == "members") {
                val res =
                    teamService.getByIdWithMembers(pathParams.id)
                        ?: return@get call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                val teamDetailsJson = TeamDetailsJSON.fromTeamDetails(res)
                call.response.status(HttpStatusCode.OK)
                call.respond(teamDetailsJson)
            } else {
                val res =
                    teamService.getById(pathParams.id)
                        ?: return@get call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                val teamJson = TeamJSON.fromTeam(res)
                call.response.status(HttpStatusCode.OK)
                call.respond(teamJson)
            }
    }

    put<TeamResource.Id> { pathParams ->
        val body = call.receive<UpdateTeamRequest>()
        val updatedTeam =
            Team(
                id = pathParams.id,
                name = body.name,
                location = Location(body.location.district),
                manager = body.manager?.let { Person.create(it) },
            )
        val res = teamService.updateTeam(updatedTeam)
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

    delete<TeamResource.Id> { pathParams ->
        val res = teamService.deleteTeam(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.NoContent)
            is Left ->
                when (res.value) {
                    TeamDeletingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                    TeamDeletingError.TeamInfoIsInvalid -> call.respondProblem(Problem.teamInfoIsInvalid, HttpStatusCode.BadRequest)
                }
        }
    }

    get<TeamResource.Id.Sellers> { pathParams ->
        val res = teamService.getTeamSellers(pathParams.parent.id)
        when (res) {
            is Right -> {
                val sellersJson = res.value.map { seller -> SellerJSON.fromSeller(seller) }
                call.respond(sellersJson)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }

    put<TeamResource.Id.Sellers> { pathParams ->
        val teamId = pathParams.parent.id
        val body = call.receive<AddTeamSellerRequest>()
        if (teamId != body.teamId.toInt()) {
            call.respondProblem(
                Problem.badRequest,
                HttpStatusCode.BadRequest,
            ) // id from path and from payload are diff
        }

        val res = teamService.addSellerToTeam(pathParams.parent.id, body.sellerId.toInt())

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }

    delete<TeamResource.Id.Sellers.Id> { pathParams ->
        val sellerId = pathParams.parent.parent.id
        val res = teamService.deleteSellerFromTeam(sellerId)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left ->
                when (res.value) {
                    TeamSellersReadingError.TeamNotFound -> call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
                }
        }
    }
}