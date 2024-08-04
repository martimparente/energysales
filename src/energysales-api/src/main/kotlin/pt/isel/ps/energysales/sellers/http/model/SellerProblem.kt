package pt.isel.ps.energysales.sellers.http.model

import io.ktor.http.HttpStatusCode
import pt.isel.ps.energysales.plugins.Problem

private const val PROBLEM_URL = "https://github.com/martimparente/ecoenergy/docs/client-documentation/problems/"

data class SellerProblem(
    override val title: String,
    override val type: String,
    override val instance: String,
    override val status: HttpStatusCode,
    override val detail: String? = null,
) : Problem {
    companion object {
        val sellerNotFound =
            SellerProblem(
                title = "Seller Not Found",
                type = PROBLEM_URL + "sellerNotFound.md",
                instance = "",
                status = HttpStatusCode.NotFound,
                detail = "Seller not found",
            )

        val sellerAlreadyExists =
            SellerProblem(
                title = "Seller Already Exists",
                type = PROBLEM_URL + "sellerAlreadyExists.md",
                instance = "",
                status = HttpStatusCode.Conflict,
                detail = "A seller with the specified email already exists",
            )

        val sellerIsInvalid =
            SellerProblem(
                title = "Invalid Seller",
                type = PROBLEM_URL + "sellerIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Seller must have between 6 to 15 characters",
            )

        val sellerEmailIsInvalid =
            SellerProblem(
                title = "Invalid Email",
                type = PROBLEM_URL + "sellerEmailIsInvalid.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "Email is invalid",
            )
        val badRequest =
            SellerProblem(
                title = "Bad Request",
                type = PROBLEM_URL + "badRequest.md",
                instance = "",
                status = HttpStatusCode.BadRequest,
                detail = "The request is invalid",
            )

        val forbidden =
            SellerProblem(
                title = "Forbidden",
                type = PROBLEM_URL + "forbidden.md",
                instance = "",
                status = HttpStatusCode.Forbidden,
                detail = "The request is forbidden",
            )
        val sellerProblem =
            SellerProblem(
                title = "Seller Problem",
                type = PROBLEM_URL + "sellerProblem.md",
                instance = "",
                status = HttpStatusCode.InternalServerError,
                detail = "An error occurred while processing the request",
            )
    }
}
