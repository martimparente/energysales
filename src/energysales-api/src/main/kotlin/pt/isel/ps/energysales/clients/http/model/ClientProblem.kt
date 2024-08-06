package pt.isel.ps.energysales.clients.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class ClientProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val partnerNotFound =
            ClientProblem(
                title = "Partner Not Found",
                type = PROBLEM_URL + "partnerNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified partner was not found",
            )

        val noPartnerProvided =
            ClientProblem(
                title = "No Partner Provided",
                type = PROBLEM_URL + "noPartnerProvided.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "No partner information was provided",
            )

        val invalidToken =
            ClientProblem(
                title = "Invalid Token",
                type = PROBLEM_URL + "invalidToken.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The provided token is invalid",
            )

        val unauthorized =
            ClientProblem(
                title = "Unauthorized",
                type = PROBLEM_URL + "unauthorized.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "You are not authorized to perform this action",
            )

        val noPartnersFound =
            ClientProblem(
                title = "No Partners Found",
                type = PROBLEM_URL + "noPartnersFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "No partners were found",
            )

        val partnerInfoIsInvalid =
            ClientProblem(
                title = "Invalid Partner Information",
                type = PROBLEM_URL + "partnerNameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided partner information is invalid",
            )

        val partnerAlreadyExists =
            ClientProblem(
                title = "Partner Already Exists",
                type = PROBLEM_URL + "partnerAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A partner with the specified name already exists",
            )

        val internalServerError =
            ClientProblem(
                title = "Internal Server Error",
                type = PROBLEM_URL + "InternalServerError.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "There was a server error :( Please try again later",
            )

        // Client-related problems
        val clientAlreadyExists =
            ClientProblem(
                title = "Client Already Exists",
                type = PROBLEM_URL + "clientAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A client with the specified information already exists",
            )

        val clientNotFound =
            ClientProblem(
                title = "Client Not Found",
                type = PROBLEM_URL + "clientNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified client was not found",
            )

        val clientInfoIsInvalid =
            ClientProblem(
                title = "Invalid Client Information",
                type = PROBLEM_URL + "clientInfoIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided client information is invalid",
            )

        val clientEmailAlreadyInUse =
            ClientProblem(
                title = "Client Email Already In Use",
                type = PROBLEM_URL + "clientEmailAlreadyInUse.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "The provided email is already in use by another client",
            )

        val badRequest =
            ClientProblem(
                title = "Bad Request",
                type = PROBLEM_URL + "badRequest.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The request was invalid or cannot be otherwise served",
            )

        val todo =
            ClientProblem(
                title = "Unauthorized",
                type = PROBLEM_URL + "unauthorized.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The request has not been applied because it lacks valid authentication credentials for the target resource",
            )
    }
}
