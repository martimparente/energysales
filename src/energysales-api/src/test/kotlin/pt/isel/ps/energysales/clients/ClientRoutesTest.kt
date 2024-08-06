package pt.isel.ps.energysales.clients

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.energysales.BaseRouteTest
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.clients.http.model.ClientJSON
import pt.isel.ps.energysales.clients.http.model.ClientProblem
import pt.isel.ps.energysales.clients.http.model.CreateClientRequest
import pt.isel.ps.energysales.clients.http.model.LocationJSON
import pt.isel.ps.energysales.clients.http.model.PatchClientRequest
import pt.isel.ps.energysales.plugins.ProblemJSON
import pt.isel.ps.energysales.users.http.model.UserProblem
import kotlin.test.Test

class ClientRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Client - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.CLIENTS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateClientRequest("newClient", "123456789", "123456789", "email1@mail.com", LocationJSON("Lisboa")))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.CLIENTS}/2")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }
        }

    @Test
    fun `Create Client - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.CLIENTS) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(CreateClientRequest("newClient", "123456789", "123456789", "email1@mail.com", LocationJSON("Lisboa")))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.unauthorized.type)
                    response.shouldHaveStatus(ClientProblem.unauthorized.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
        // todo
        @Test
        fun `Create Client - Forbidden - No permission Roles`() = testApplication {
            testClient().post(Uris.API + Uris.CLIENT) {
                headers.append("Authorization", "Bearer $token")
                setBody(CreateClientRequest("newClient", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }
        @Test
        fun `Create Client - Bad Request`() = testApplication {
            testClient().post(Uris.API + Uris.CLIENT) {
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.noClientProvided.type)
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }

    // TODO BECAUSE OF NIF OR PHONE ARE RANDOM GENERATED, THIS TEST WILL FAIL
    @Test
    fun `Create Client - Client already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.CLIENT) {
                    headers.append("Authorization", "Bearer $token")
                    setBody(CreateClientRequest("Client 1", "123456789", "123456789", "District 1"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.clientAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }*/

    @Test
    fun `Get Client by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                }.also { response ->
                    val client = response.call.response.body<ClientJSON>()
                    client.name.shouldBe("Client 1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Client by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.clientNotFound.type)
                    response.shouldHaveStatus(ClientProblem.clientNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Client by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.badRequest.type)
                    response.shouldHaveStatus(ClientProblem.badRequest.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Clients - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.CLIENTS) {
                    headers.append("Authorization", "Bearer $adminToken")
                }.also { response ->
                    response.body<List<ClientJSON>>()
                }
        }

    @Test
    fun `Update Client - Success`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(PatchClientRequest("updateName", "123456789", "email1@mail.com", LocationJSON("Lisboa"), "1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Update Client - Unauthorized`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.CLIENTS_BY_ID) {
                    parameter("id", "2")
                    setBody(PatchClientRequest("newClient", "123456789", "email1@mail.com", LocationJSON("Lisboa")))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.unauthorized.type)
                    response.shouldHaveStatus(UserProblem.unauthorized.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
      // todo
      @Test
        fun `Update Client - Forbidden`() = testApplication {
            testClient().put(Uris.API + Uris.CLIENT_BY_ID) {
                setBody(UpdateClientRequest("newClient", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Update Client - Not Found`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                    setBody(PatchClientRequest("nonClient", "123456789", "email1@mail.com", LocationJSON("Lisboa")))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.clientNotFound.type)
                    response.shouldHaveStatus(ClientProblem.clientNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Client - Bad Request - Invalid E-mail`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(PatchClientRequest("client", "123456789", "invalidEmail", LocationJSON("Lisboa"), "1"))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.badRequest.type)
                    response.shouldHaveStatus(ClientProblem.badRequest.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Client - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Client - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("id", "1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.unauthorized.type)
                    response.shouldHaveStatus(UserProblem.unauthorized.status)
                }
        }

    @Test
    fun `Delete Client - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.clientNotFound.type)
                    response.shouldHaveStatus(ClientProblem.clientNotFound.status)
                }
        }

    @Test
    fun `Delete Client - Client Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.clientNotFound.type)
                    response.shouldHaveStatus(ClientProblem.clientNotFound.status)
                }
        }

    @Test
    fun `Delete Client - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.CLIENTS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(ClientProblem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }
}
