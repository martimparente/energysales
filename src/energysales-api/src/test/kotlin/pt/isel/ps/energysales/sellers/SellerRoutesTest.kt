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
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.energysales.BaseRouteTest
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.plugins.ProblemJSON
import pt.isel.ps.energysales.sellers.http.model.CreateSellerRequest
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.sellers.http.model.SellerProblem
import pt.isel.ps.energysales.users.http.model.UserProblem
import kotlin.test.Test

class SellerRoutesTest : BaseRouteTest() {
    private val testToken = managerToken

    @Test
    fun `Create Seller - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SELLERS) {
                    headers.append("Authorization", "Bearer $testToken")
                    setBody(CreateSellerRequest("name", "surname", "email", "1"))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.SELLERS}/14")
                    response.shouldHaveStatus(HttpStatusCode.Created)
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
                    setBody(CreateSellerRequest("name", "surname", "email", "1"))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.unauthorized.type)
                    response.shouldHaveStatus(UserProblem.unauthorized.status)
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
                    setBody(CreateSellerRequest("name", "surname", "email", "1"))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Seller by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", 1)
                }.also { response ->
                    val seller = response.call.response.body<SellerJSON>()
                    seller.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Seller by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(SellerProblem.sellerNotFound.type)
                    response.shouldHaveStatus(SellerProblem.sellerNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Seller by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(SellerProblem.badRequest.type)
                    response.shouldHaveStatus(SellerProblem.badRequest.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Sellers - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SELLERS) {
                    headers.append("Authorization", "Bearer $testToken")
                }.also { response ->
                    response.body<List<SellerJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    /*
    todo fix updates
    @Test
     fun `Update Seller - Success`() =
         testApplication {
             testClient()
                 .put(Uris.API + Uris.SELLERS_BY_ID) {
                     headers.append("Authorization", "Bearer $testToken")
                     parameter("id", 1)
                     setBody(UpdateSellerRequest("1", 1.0f))
                 }.also { response ->
                     response.shouldHaveStatus(HttpStatusCode.OK)
                 }
         }

     @Test
     fun `Update Seller - Unauthorized`() =
         testApplication {
             testClient()
                 .put(Uris.API + Uris.SELLERS_BY_ID) {
                     parameter("id", 2)
                     setBody(UpdateSellerRequest("1", 0.0f))
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
                     headers.append("Authorization", "Bearer $testToken")
                     parameter("id", 2)
                     setBody(UpdateSellerRequest("1", 0.0f))
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
                     headers.append("Authorization", "Bearer $testToken")
                     parameter("id", -1)
                     setBody(UpdateSellerRequest("-1", 0.0f))
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
                     headers.append("Authorization", "Bearer $testToken")
                     parameter("id", "abc")
                     setBody(UpdateSellerRequest("1", 0.0f))
                 }.also { response ->
                     response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                     response.shouldHaveStatus(HttpStatusCode.BadRequest)
                     response.shouldHaveContentType(ContentType.Application.ProblemJson)
                 }
         }
     */
    @Test
    fun `Delete Seller - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
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
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.unauthorized.type)
                    response.shouldHaveStatus(UserProblem.unauthorized.status)
                }
        }

    @Test
    fun `Delete Seller - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(SellerProblem.sellerNotFound.type)
                    response.shouldHaveStatus(SellerProblem.sellerNotFound.status)
                }
        }

    @Test
    fun `Delete Seller - Seller Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(SellerProblem.sellerNotFound.type)
                    response.shouldHaveStatus(SellerProblem.sellerNotFound.status)
                }
        }

    @Test
    fun `Delete Seller - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SELLERS_BY_ID) {
                    headers.append("Authorization", "Bearer $testToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(SellerProblem.badRequest.type)
                    response.shouldHaveStatus(SellerProblem.badRequest.status)
                }
        }
}
