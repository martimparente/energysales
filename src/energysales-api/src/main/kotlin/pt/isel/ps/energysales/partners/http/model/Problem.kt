package pt.isel.ps.energysales.partners.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class PartnerProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val partnerNotFound =
            PartnerProblem(
                title = "Partner Not Found",
                type = PROBLEM_URL + "partnerNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified partner was not found",
            )

        val noPartnerProvided =
            PartnerProblem(
                title = "No Partner Provided",
                type = PROBLEM_URL + "noPartnerProvided.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "No partner information was provided",
            )

        val invalidToken =
            PartnerProblem(
                title = "Invalid Token",
                type = PROBLEM_URL + "invalidToken.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "The provided token is invalid",
            )

        val unauthorized =
            PartnerProblem(
                title = "Unauthorized",
                type = PROBLEM_URL + "unauthorized.md",
                instance = "",
                status = HttpStatusCode.Unauthorized,
                detail = "You are not authorized to perform this action",
            )

        val noPartnersFound =
            PartnerProblem(
                title = "No Partners Found",
                type = PROBLEM_URL + "noPartnersFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "No partners were found",
            )

        val partnerInfoIsInvalid =
            PartnerProblem(
                title = "Invalid Partner Information",
                type = PROBLEM_URL + "partnerNameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The provided partner information is invalid",
            )

        val partnerAlreadyExists =
            PartnerProblem(
                title = "Partner Already Exists",
                type = PROBLEM_URL + "partnerAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A partner with the specified name already exists",
            )

        val internalServerError =
            PartnerProblem(
                title = "Internal Server Error",
                type = PROBLEM_URL + "InternalServerError.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "There was a server error :( Please try again later",
            )
        val avatarImgNotFound =
            PartnerProblem(
                title = "Avatar Image Not Found",
                type = PROBLEM_URL + "avatarImgNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "The specified avatar image was not found",
            )
    }
}
