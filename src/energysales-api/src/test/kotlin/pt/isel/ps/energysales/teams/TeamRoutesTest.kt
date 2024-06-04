package pt.isel.ps.energysales.teams

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
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.http.model.AddTeamSellerRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.LocationJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.teams.http.model.UpdateTeamRequest
import kotlin.test.Test

class TeamRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Team - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $token")
                    setBody(CreateTeamRequest("newTeam", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.TEAMS}/51")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Create Team - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(CreateTeamRequest("newTeam", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
        // todo
        @Test
        fun `Create Team - Forbidden - No permission Roles`() = testApplication {
            testClient().post(Uris.API + Uris.TEAMS) {
                headers.append("Authorization", "Bearer $token")
                setBody(CreateTeamRequest("newTeam", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }
        @Test
        fun `Create Team - Bad Request`() = testApplication {
            testClient().post(Uris.API + Uris.TEAMS) {
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.noTeamProvided.type)
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Create Team - Team already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $token")
                    setBody(CreateTeamRequest("Team 1", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.teamAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Team by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "1")
                }.also { response ->
                    val team = response.call.response.body<TeamJSON>()
                    team.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Get Team by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", -1)
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.teamNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Team by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "paramTypeInvalid")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Teams - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $token")
                }.also { response ->
                    response.body<List<TeamJSON>>()
                }
        }

    @Test
    fun `Update Team - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", 2)
                    setBody(UpdateTeamRequest("updatedTeam", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Update Team - Unauthorized`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_BY_ID) {
                    parameter("teamId", 2)
                    setBody(UpdateTeamRequest("newTeam", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
      // todo
      @Test
        fun `Update Team - Forbidden`() = testApplication {
            testClient().put(Uris.API + Uris.TEAMS_BY_ID) {
                setBody(UpdateTeamRequest("newTeam", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Update Team - Not Found`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", -1)
                    setBody(UpdateTeamRequest("nonExistingTeam", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.teamNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Team - Bad Request`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "abc")
                    setBody(UpdateTeamRequest("", LocationJSON("newDistrict"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Team - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NoContent)
                }
        }

    @Test
    fun `Delete Team - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("teamId", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Team - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Team - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }

    @Test
    fun `Get Team Sellers - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_SELLERS) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "1")
                }.also { response ->
                    response.body<List<SellerJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Add Seller to Team - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_SELLERS) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "1")
                    setBody(AddTeamSellerRequest("1", "1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Delete Seller from Team - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_SELLER) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("teamId", "1")
                    parameter("sellerId", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }
}
