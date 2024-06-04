package pt.isel.ps.energysales.products

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
import pt.isel.ps.energysales.products.http.model.CreateProductRequest
import pt.isel.ps.energysales.products.http.model.ProductJSON
import pt.isel.ps.energysales.products.http.model.UpdateProductRequest
import kotlin.test.Test

class ProductRoutesTest : BaseRouteTest() {
    @Test
    fun `Create Product - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PRODUCT) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateProductRequest("newProduct", 0.0, ""))
                }.also { response ->
                    response.headers["Location"]?.shouldBeEqual("${Uris.PRODUCT}/51")
                    response.shouldHaveStatus(HttpStatusCode.Created)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Create Product - Unauthorized`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PRODUCT) {
                    headers.append(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                            "eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTM1Njk1MDl9." +
                            "PujUDxkJjBeo8viQELQquH5zeW9P_LfS1jYBNmXIOAY",
                    )
                    setBody(CreateProductRequest("newProduct", 0.0, ""))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
        // todo
        @Test
        fun `Create Product - Forbidden - No permission Roles`() = testApplication {
            testClient().post(Uris.API + Uris.PRODUCT) {
                headers.append("Authorization", "Bearer $token")
                setBody(CreateProductRequest("newProduct", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }
        @Test
        fun `Create Product - Bad Request`() = testApplication {
            testClient().post(Uris.API + Uris.PRODUCT) {
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.noProductProvided.type)
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Create Product - Product already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.PRODUCT) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateProductRequest("Product 1", 0.0, ""))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.productAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Product by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                }.also { response ->
                    val product = response.call.response.body<ProductJSON>()
                    product.name.shouldBe("Product 1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Get Product by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.productNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Product by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get All Products - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.PRODUCT) {
                    headers.append("Authorization", "Bearer $adminToken")
                }.also { response ->
                    response.body<List<ProductJSON>>()
                }
        }

    @Test
    fun `Update Product - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 2)
                    setBody(UpdateProductRequest("updatedProduct", 0.0, ""))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Update Product - Unauthorized`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PRODUCT_BY_ID) {
                    parameter("id", 2)
                    setBody(UpdateProductRequest("newProduct", 0.0, ""))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    /*
      // todo
      @Test
        fun `Update Product - Forbidden`() = testApplication {
            testClient().put(Uris.API + Uris.PRODUCT_BY_ID) {
                setBody(UpdateProductRequest("newProduct", "newLocation", null))
            }.also { response ->
                response.body<Problem>().type.shouldBeEqual(Problem.unauthorized.type)
                response.shouldHaveStatus(HttpStatusCode.Forbidden)
                response.shouldHaveContentType(ContentType.Application.ProblemJson)
            }
        }*/

    @Test
    fun `Update Product - Not Found`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                    setBody(UpdateProductRequest("nonExistingProduct", 0.0, ""))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.productNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update Product - Bad Request`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "abc")
                    setBody(UpdateProductRequest("", 0.0, ""))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.badRequest.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Product - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 3)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NoContent)
                }
        }

    @Test
    fun `Delete Product - Unauthorized`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Invalid Token")
                    parameter("id", 1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }
        }

    @Test
    fun `Delete Product - Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Product - Product Not Found`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                }
        }

    @Test
    fun `Delete Product - Bad Request`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.PRODUCT_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }
        }
}
