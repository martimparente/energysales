package pt.isel.ps.energysales.sellers

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.energysales.BaseRouteTest
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.sellers.http.model.CreateSellerRequest
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.sellers.http.model.UpdateSellerRequest
import kotlin.test.Test

class SellerRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Seller - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateSellerRequest("name", "surname", "email@email"))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.SELLERS}/51")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Create Seller - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SELLERS) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(CreateSellerRequest("newSeller", "newLocation", "test@test.pt"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Seller - Forbidden - No permission Roles`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SELLERS) {
                    headers.append(
                        "Authorization",
                        "Bearer $sellerToken",
                    )
                    setBody(CreateSellerRequest("newSeller", "newLocation", "test@test.pt"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Seller - email already in use`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateSellerRequest("name", "surname", "1@mail.com"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.sellerEmailAlreadyInUse.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Seller by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                }.also { response ->
                    val seller = response.call.response.body<SellerJSON>()
                    seller.person.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Get Seller by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.sellerNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Seller by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Sellers - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                }.also { response ->
                    response.body<List<SellerJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Update Seller - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                    setBody(UpdateSellerRequest("newSeller", "newLocation", "1@mail.com", 0.0f))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Update Seller - Unauthorized`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SELLERS_BY_ID) {
                    parameter("id", 2)
                    setBody(UpdateSellerRequest("newSeller", "newLocation", "test@test.pt", 0.0f))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Seller - Forbidden`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("id", 2)
                    setBody(UpdateSellerRequest("newSeller", "newLocation", "test@test.pt", 0.0f))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Seller - Not Found`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                    setBody(UpdateSellerRequest("newSeller", "newLocation", "test@test.pt", 0.0f))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.sellerNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Seller - Bad Request`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "abc")
                    setBody(UpdateSellerRequest("newSeller", "newLocation", "test@test.pt", 0.0f))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Seller - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NoContent)
                }
        }

    @Test
    fun `Delete Seller - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("id", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Seller - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Seller - Seller Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Seller - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }
}
