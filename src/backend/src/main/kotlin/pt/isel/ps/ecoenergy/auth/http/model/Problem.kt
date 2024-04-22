package pt.isel.ps.ecoenergy.auth.http.model

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

/**
 * Responds with a problem+json in the response body
 * @param status The status code of the response
 * @param problem The problem to be sent in the response body
 * @see Problem
 */
suspend fun ApplicationCall.respondProblem(
    problem: Problem,
    status: HttpStatusCode,
) {
    response.header("Content-Type", ContentType.Application.ProblemJson.toString())
    response.status(status)
    val mutatedProblem = problem.copy(instance = request.uri)
    respond(mutatedProblem)
}

@Serializable
data class Problem(
    val title: String,
    val type: String,
    val instance: String,
) {
    companion object {
        private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

        val badRequest: Problem =
            Problem(
                "Some parameters are invalid",
                PROBLEM_URL + "badRequest.md",
                "",
            )

        val teamNotFound: Problem =
            Problem(
                "Team was not found",
                PROBLEM_URL + "teamNotFound.md",
                "",
            )

        val noTeamProvided: Problem =
            Problem(
                "No team information provided",
                PROBLEM_URL + "noTeamProvided.md",
                "",
            )

        val invalidToken: Problem =
            Problem(
                "Invalid Token",
                PROBLEM_URL + "invalidToken.md",
                "",
            )

        val unauthorized: Problem =
            Problem(
                "Unauthorized",
                PROBLEM_URL + "unauthorized.md",
                "",
            )

        val noTeamsFound: Problem =
            Problem(
                "No Teams Found",
                PROBLEM_URL + "noTeamsFound.md",
                "",
            )

        val teamInfoIsInvalid: Problem =
            Problem(
                "The Team information given is invalid",
                PROBLEM_URL + "teamNameIsInvalid.md",
                "",
            )

        val teamAlreadyExists: Problem =
            Problem(
                "Team already exists",
                PROBLEM_URL + "teamAlreadyExists.md",
                "",
            )

        val internalServerError =
            Problem(
                "There was a server error :( Please try again later",
                PROBLEM_URL + "InternalServerError.md",
                "",
            )

        val userAlreadyExists =
            Problem(
                "Username already exists",
                PROBLEM_URL + "UserAlreadyExists.md",
                "",
            )
        val insecurePassword =
            Problem(
                "Passwords should have at least 8 characters and at least one of the following types:\n" +
                    "Uppercase letters: A-Z\n" +
                    "Lowercase letters: a-z\n" +
                    "Numbers: 0-9\n" +
                    "Symbols: ~`!@#\$%^&*()_-+={[}]|\\:;\"'<,>.?/",
                PROBLEM_URL + "insecurePassword.md",
                "",
            )
        val userOrPasswordAreInvalid =
            Problem(
                "Username or Password invalid",
                PROBLEM_URL + "userOrPasswordAreInvalid.md",
                "",
            )
        val userIsInvalid =
            Problem(
                "User must have between 6 to 15 characters",
                PROBLEM_URL + "userIsInvalid.md",
                "",
            )
        val userNotFound =
            Problem(
                "User not found",
                PROBLEM_URL + "userNotFound.md",
                "",
            )
        val passwordMismatch =
            Problem(
                "Passwords don't match",
                PROBLEM_URL + "passwordMismatch.md",
                "",
            )
    }
}
