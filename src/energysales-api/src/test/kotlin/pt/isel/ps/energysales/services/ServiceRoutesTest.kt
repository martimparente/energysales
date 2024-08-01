package pt.isel.ps.energysales.services

import CreateServiceRequest
import UpdateServiceRequest
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
import pt.isel.ps.energysales.services.http.model.PriceJSON
import pt.isel.ps.energysales.services.http.model.ServiceJSON
import pt.isel.ps.energysales.users.http.model.Problem
import kotlin.test.Test

class ServiceRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Service - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SERVICES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    val body =
                        CreateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        )
                    setBody(body)
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.SERVICES}/11")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }
        }

    @Test
    fun `Create Service - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SERVICES) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(
                        CreateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
        // todo
        @Test
        fun `Create Service - Forbidden - No permission Roles`() = testApplication {
            testClient().post(Uris.API + Uris.PRODUCT) {
                headers.append("Authorization", "Bearer $token")
                setBody(CreateServiceRequest("newService", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }
        @Test
        fun `Create Service - Bad Request`() = testApplication {
            testClient().post(Uris.API + Uris.PRODUCT) {
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.noServiceProvided.type)
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }
          @Test
    fun `Create Service - Service already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.SERVICES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateServiceRequest("newService", "newDescription", "newCycleName", "newCycleType", "newPeriodName", 1))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.serviceAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }*/

    @Test
    fun `Get Service by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                }.also { response ->
                    val service = response.call.response.body<ServiceJSON>()
                    service.name.shouldBe("Service 1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Service by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.serviceNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Service by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Services - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.SERVICES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    url {
                        parameters.append("lastKeySeen", "3")
                    }
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<List<ServiceJSON>>()
                }
        }

    @Test
    fun `Update Service - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "2")
                    setBody(
                        UpdateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1904f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        ),
                    )
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    val service = response.call.response.body<ServiceJSON>()
                    service.name.shouldBe("newService")
                    service.price.shouldBe(PriceJSON(0.1904f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f))
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Update Service - Unauthorized`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SERVICES_BY_ID) {
                    parameter("id", "2")
                    setBody(
                        UpdateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
      // todo
      @Test
        fun `Update Service - Forbidden`() = testApplication {
            testClient().put(Uris.API + Uris.PRODUCT_BY_ID) {
                setBody(UpdateServiceRequest("newService", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Update Service - Not Found`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                    setBody(
                        UpdateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.serviceNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Service - Bad Request`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "abc")
                    setBody(
                        UpdateServiceRequest(
                            "newService",
                            "newDescription",
                            "newCycleName",
                            "newCycleType",
                            "newPeriodName",
                            1,
                            PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Service - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Service - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("id", 1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Service - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Service - Service Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Service - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.SERVICES_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }
}
