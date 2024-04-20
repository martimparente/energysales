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
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import pt.isel.ps.ecoenergy.Uris
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.respondProblem
import pt.isel.ps.ecoenergy.team.domain.service.TeamCreationError
import pt.isel.ps.ecoenergy.team.domain.service.TeamService
import pt.isel.ps.ecoenergy.team.http.model.CreateTeamRequest
import pt.isel.ps.ecoenergy.team.http.model.TeamResponse
import pt.isel.ps.ecoenergy.team.http.model.UpdateTeamRequest

@Resource(Uris.TEAMS)
class TeamResource(val lastKeySeen: Int? = null) {
    @Resource("{id}")
    class Id(val parent: TeamResource = TeamResource(), val id: Int)
}

fun Route.teamRoutes(teamService: TeamService) {

    get<TeamResource> { queryParams ->
        val teams = teamService.getAllTeamsPaging(10, queryParams.lastKeySeen)
        val teamsResponse = teams.map { team -> TeamResponse.fromTeam(team) }
        when (teamsResponse.isNotEmpty()) {
            true -> call.respond(teamsResponse)
            else -> call.respondProblem(Problem.noTeamsFound, HttpStatusCode.NotFound)
        }
    }

    post<TeamResource> {
        val input = call.receive<CreateTeamRequest>()
        val res = teamService.createTeam(input.name, input.location, input.manager)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.TEAMS}/${res.value}")
            }

            is Left -> when (res.value) {
                TeamCreationError.TeamAlreadyExists -> call.respondProblem(Problem.teamAlreadyExists, HttpStatusCode.Conflict)
                TeamCreationError.TeamNameIsInvalid -> call.respondProblem(Problem.teamNameIsInvalid, HttpStatusCode.BadRequest)
            }
        }
    }

    get<TeamResource.Id> { team ->
        // Show an teamwith id ${article.id} ...
        call.respondText("An teamwith id ${team.id}", status = HttpStatusCode.OK)
    }

    put<TeamResource.Id> { pathParams ->
        val input = call.receive<UpdateTeamRequest>()
        val res = teamService.updateTeam(pathParams.id, input.name, input.location, input.manager)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.TEAMS}/${res.value}")
            }

            is Left -> when (res.value) {
                TeamCreationError.TeamAlreadyExists -> call.respondProblem(Problem.teamAlreadyExists, HttpStatusCode.Conflict)
                TeamCreationError.TeamNameIsInvalid -> call.respondProblem(Problem.teamNameIsInvalid, HttpStatusCode.BadRequest)
            }
        }
    }
    put<TeamResource.Id> { team ->
        // Update a team...
        call.respondText("An teamwith id ${team.id} updated", status = HttpStatusCode.OK)
    }
    delete<TeamResource.Id> { team ->
        // Delete an team...
        call.respondText("An teamwith id ${team.id} deleted", status = HttpStatusCode.OK)
    }
}

