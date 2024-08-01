package pt.isel.ps.energysales.teams

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
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.energysales.BaseRouteTest
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.teams.http.model.AddTeamClientRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamSellerRequest
import pt.isel.ps.energysales.teams.http.model.AddTeamServiceRequest
import pt.isel.ps.energysales.teams.http.model.CreateTeamRequest
import pt.isel.ps.energysales.teams.http.model.LocationJSON
import pt.isel.ps.energysales.teams.http.model.TeamDetailsJSON
import pt.isel.ps.energysales.teams.http.model.TeamJSON
import pt.isel.ps.energysales.teams.http.model.UpdateTeamRequest
import pt.isel.ps.energysales.users.http.model.Problem
import kotlin.test.Test

class TeamRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Team - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateTeamRequest("newTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.TEAMS}/11")
                    response.shouldHaveStatus(HttpStatusCode.Created)
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
                    setBody(CreateTeamRequest("newTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Team - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    setBody(CreateTeamRequest("newTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Team - Team already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateTeamRequest("Team 1", LocationJSON("Lisboa"), null))
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
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                }.also { response ->
                    val team = response.call.response.body<TeamJSON>()
                    team.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Team by ID with Details - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    url {
                        parameters.append("teamId", "1")
                        parameters.append("include", "details")
                    }
                }.also { response ->
                    val teamDeails = response.call.response.body<TeamDetailsJSON>()
                    teamDeails.team.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Team by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
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
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "paramTypeInvalid")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Team by ID - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("teamId", "1")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Teams - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS) {
                    headers.append("Authorization", "Bearer $adminToken")
                }.also { response ->
                    response.body<List<TeamJSON>>()
                }
        }

    @Test
    fun `Update Team - Success`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", 2)
                    setBody(UpdateTeamRequest("updatedTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Update Team - Unauthorized`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.TEAMS_BY_ID) {
                    parameter("teamId", 2)
                    setBody(UpdateTeamRequest("newTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Team - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("teamId", 2)
                    setBody(UpdateTeamRequest("newTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Team - Not Found`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "-1")
                    setBody(UpdateTeamRequest("nonExistingTeam", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.teamNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Team - Bad Request - Invalid Name`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    setBody(UpdateTeamRequest("", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.teamInfoIsInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Team - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "3")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Team - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("teamId", "3")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Team - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "-1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Team - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }

    @Test
    fun `Delete Team - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("teamId", "2")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Team Sellers - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.TEAMS_SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                }.also { response ->
                    response.body<List<SellerJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    // TODO MORE TESTES

    @Test
    fun `Add Seller to Team - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    setBody(AddTeamSellerRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Seller from Team - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_SELLER) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    parameter("sellerId", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Assign Service to Team - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_SERVICES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    setBody(AddTeamServiceRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Service from Team - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.TEAMS_SERVICE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    parameter("serviceId", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Assign Client to Team - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.TEAMS_CLIENTS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("teamId", "1")
                    parameter("clientId", "1")
                    setBody(AddTeamClientRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }
}
