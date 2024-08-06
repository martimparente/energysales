package pt.isel.ps.energysales.partners

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
import pt.isel.ps.energysales.partners.http.model.AddPartnerClientRequest
import pt.isel.ps.energysales.partners.http.model.AddPartnerSellerRequest
import pt.isel.ps.energysales.partners.http.model.AddPartnerServiceRequest
import pt.isel.ps.energysales.partners.http.model.CreatePartnerRequest
import pt.isel.ps.energysales.partners.http.model.LocationJSON
import pt.isel.ps.energysales.partners.http.model.PartnerDetailsJSON
import pt.isel.ps.energysales.partners.http.model.PartnerJSON
import pt.isel.ps.energysales.partners.http.model.PartnerProblem
import pt.isel.ps.energysales.partners.http.model.UpdatePartnerRequest
import pt.isel.ps.energysales.plugins.ProblemJSON
import pt.isel.ps.energysales.sellers.http.model.SellerJSON
import pt.isel.ps.energysales.users.http.model.UserProblem
import kotlin.test.Test

class PartnerRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Partner - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PARTNERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreatePartnerRequest("newPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.PARTNERS}/11")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }
        }

    @Test
    fun `Create Partner - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PARTNERS) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(CreatePartnerRequest("newPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.unauthorized.type)
                    response.shouldHaveStatus(PartnerProblem.unauthorized.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Partner - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PARTNERS) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    setBody(CreatePartnerRequest("newPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create Partner - Partner already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PARTNERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreatePartnerRequest("Partner 1", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.partnerAlreadyExists.type)
                    response.shouldHaveStatus(PartnerProblem.partnerAlreadyExists.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Partner by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                }.also { response ->
                    val partner = response.call.response.body<PartnerJSON>()
                    partner.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Partner by ID with Details - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    url {
                        parameters.append("partnerId", "1")
                        parameters.append("include", "details")
                    }
                }.also { response ->
                    val partnerDeails = response.call.response.body<PartnerDetailsJSON>()
                    partnerDeails.partner.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get Partner by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", -1)
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.partnerNotFound.type)
                    response.shouldHaveStatus(PartnerProblem.partnerNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Partner by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.badRequest.type)
                    response.shouldHaveStatus(UserProblem.badRequest.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Partner by ID - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("partnerId", "1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Partners - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                }.also { response ->
                    response.body<List<PartnerJSON>>()
                }
        }

    @Test
    fun `Update Partner - Success`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", 2)
                    setBody(UpdatePartnerRequest("updatedPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Update Partner - Unauthorized`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.PARTNERS_BY_ID) {
                    parameter("partnerId", 2)
                    setBody(UpdatePartnerRequest("newPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.unauthorized.type)
                    response.shouldHaveStatus(PartnerProblem.unauthorized.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Partner - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("partnerId", 2)
                    setBody(UpdatePartnerRequest("newPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Partner - Not Found`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "-1")
                    setBody(UpdatePartnerRequest("nonExistingPartner", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.partnerNotFound.type)
                    response.shouldHaveStatus(PartnerProblem.partnerNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Partner - Bad Request - Invalid Name`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    setBody(UpdatePartnerRequest("", LocationJSON("Lisboa"), null))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(PartnerProblem.partnerInfoIsInvalid.type)
                    response.shouldHaveStatus(PartnerProblem.partnerInfoIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Partner - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "3")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Partner - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("partnerId", "3")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Partner - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "-1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Partner - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }

    @Test
    fun `Delete Partner - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("partnerId", "2")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Partner Sellers - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PARTNERS_SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                }.also { response ->
                    response.body<List<SellerJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    // TODO MORE TESTES

    @Test
    fun `Add Seller to Partner - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PARTNERS_SELLERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    setBody(AddPartnerSellerRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Seller from Partner - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_SELLER) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    parameter("sellerId", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Assign Service to Partner - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PARTNERS_SERVICES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    setBody(AddPartnerServiceRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Delete Service from Partner - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PARTNERS_SERVICE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    parameter("serviceId", "1")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Assign Client to Partner - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PARTNERS_CLIENTS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("partnerId", "1")
                    parameter("clientId", "1")
                    setBody(AddPartnerClientRequest("1"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }
}
