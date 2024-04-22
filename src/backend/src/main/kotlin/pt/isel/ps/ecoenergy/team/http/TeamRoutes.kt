package pt.isel.ps.ecoenergy.team.http

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
import pt.isel.ps.ecoenergy.team.domain.model.Person
import pt.isel.ps.ecoenergy.team.domain.model.Team
import pt.isel.ps.ecoenergy.team.domain.service.TeamCreationError
import pt.isel.ps.ecoenergy.team.domain.service.TeamDeletingError
import pt.isel.ps.ecoenergy.team.domain.service.TeamService
import pt.isel.ps.ecoenergy.team.domain.service.TeamUpdatingError
import pt.isel.ps.ecoenergy.team.http.model.CreateTeamRequest
import pt.isel.ps.ecoenergy.team.http.model.TeamJson
import pt.isel.ps.ecoenergy.team.http.model.UpdateTeamRequest

@Resource(Uris.TEAMS)
class TeamResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: TeamResource = TeamResource(),
        val id: Int,
    )
}

fun Route.teamRoutes(teamService: TeamService) {
    get<TeamResource> { queryParams ->
        val teams = teamService.getAllTeamsPaging(10, queryParams.lastKeySeen)
        val teamsResponse = teams.map { team -> TeamJson.fromTeam(team) }
        call.respond(teamsResponse)
    }

    post<TeamResource> {
        val body = call.receive<CreateTeamRequest>()
        val res = teamService.createTeam(body.name, body.location, body.manager)

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
        val team =
            teamService.getById(pathParams.id)
                ?: return@get call.respondProblem(Problem.teamNotFound, HttpStatusCode.NotFound)
        val teamJson = TeamJson.fromTeam(team)
        call.response.status(HttpStatusCode.OK)
        call.respond(teamJson)
    }

    put<TeamResource.Id> { pathParams ->
        val body = call.receive<UpdateTeamRequest>()
        val updatedTeam =
            Team(
                id = pathParams.id,
                name = body.name,
                location = body.location,
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
}
