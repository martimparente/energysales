package pt.isel.ps.energysales.teams.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class TeamProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val teamNotFound =
            TeamProblem(
                title = "Team Not Found",
                type = PROBLEM_URL + "teamNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified team was not found",
            )

        val noTeamProvided =
            TeamProblem(
                title = "No Team Provided",
                type = PROBLEM_URL + "noTeamProvided.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "No team information was provided",
            )

        val invalidToken =
            TeamProblem(
                title = "Invalid Token",
                type = PROBLEM_URL + "invalidToken.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The provided token is invalid",
            )

        val unauthorized =
            TeamProblem(
                title = "Unauthorized",
                type = PROBLEM_URL + "unauthorized.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "You are not authorized to perform this action",
            )

        val noTeamsFound =
            TeamProblem(
                title = "No Teams Found",
                type = PROBLEM_URL + "noTeamsFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "No teams were found",
            )

        val teamInfoIsInvalid =
            TeamProblem(
                title = "Invalid Team Information",
                type = PROBLEM_URL + "teamNameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided team information is invalid",
            )

        val teamAlreadyExists =
            TeamProblem(
                title = "Team Already Exists",
                type = PROBLEM_URL + "teamAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A team with the specified name already exists",
            )

        val internalServerError =
            TeamProblem(
                title = "Internal Server Error",
                type = PROBLEM_URL + "InternalServerError.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "There was a server error :( Please try again later",
            )
        val avatarImgNotFound =
            TeamProblem(
                title = "Avatar Image Not Found",
                type = PROBLEM_URL + "avatarImgNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified avatar image was not found",
            )
    }
}
