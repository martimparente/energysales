package pt.isel.ps.energysales.services.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class ServiceProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val serviceNotFound =
            ServiceProblem(
                title = "Service Not Found",
                type = PROBLEM_URL + "serviceNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "Seller not found",
            )
        val serviceEmailIsInvalid =
            ServiceProblem(
                title = "Invalid Email",
                type = PROBLEM_URL + "serviceEmailIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Email is invalid",
            )
        val serviceNameIsInvalid =
            ServiceProblem(
                title = "Invalid Name",
                type = PROBLEM_URL + "serviceNameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Name is invalid",
            )

        val serviceSurnameIsInvalid =
            ServiceProblem(
                title = "Invalid Surname",
                type = PROBLEM_URL + "serviceSurnameIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Surname is invalid",
            )

        val serviceAlreadyExists =
            ServiceProblem(
                title = "Service Already Exists",
                type = PROBLEM_URL + "serviceAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A service with the specified id already exists",
            )

        val serviceEmailAlreadyInUse =
            ServiceProblem(
                title = "Service Email Already In Use",
                type = PROBLEM_URL + "serviceEmailAlreadyInUse.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A service with the specified email already exists",
            )

        val serviceInfoIsInvalid =
            ServiceProblem(
                title = "Invalid Service Info",
                type = PROBLEM_URL + "serviceInfoIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Service info is invalid",
            )
        val todo =
            ServiceProblem(
                title = "TODO",
                type = PROBLEM_URL + "todo.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "Not implemented yet",
            )
        val badRequest =
            ServiceProblem(
                title = "Bad Request",
                type = PROBLEM_URL + "badRequest.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The request is invalid",
            )
    }
}
