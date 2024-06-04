package pt.isel.ps.energysales.auth

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldStartWith
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
import pt.isel.ps.energysales.auth.http.model.ChangePasswordRequest
import pt.isel.ps.energysales.auth.http.model.CreateUserRequest
import pt.isel.ps.energysales.auth.http.model.LoginRequest
import pt.isel.ps.energysales.auth.http.model.LoginResponse
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.RoleRequest
import kotlin.test.Test

class AuthRoutesTest : BaseRouteTest() {
    @Test
    fun `Create User - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateUserRequest("newTestUser", "SecurePass123!", "SecurePass123!", setOf("SELLER")))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Created)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Create User - Invalid username length`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateUserRequest("123", "SecurePass123!", "SecurePass123!", setOf("SELLER")))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Username already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateUserRequest("Username 1", "SecurePass123!", "SecurePass123!", setOf("SELLER")))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Password mismatch`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateUserRequest("testUser", "SecurePass123!", "PassSecure123!", setOf("SELLER")))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.passwordMismatch.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Insecure Password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(CreateUserRequest("testUser", "insecure", "insecure", setOf("SELLER")))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.insecurePassword.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    setBody(CreateUserRequest("doesNotMatter", "doesNotMatter", "doesNotMatter", setOf("SELLER")))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Login - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("adminUser", "SecurePass123!"))
                }.also { response ->
                    response.body<LoginResponse>().token.shouldStartWith("ey")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Login - User not found`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("nonExistUser", "SecurePass123!"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userOrPasswordAreInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Login - Password invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("testUser", "wrongPassword"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userOrPasswordAreInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Roles from User - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_ROLES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.OK)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Assign Role to User - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS_ROLES) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 3)
                    setBody(RoleRequest("ADMIN"))
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.Created)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Assign Role to User - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS_ROLES) {
                    headers.append("Authorization", "Bearer $sellerToken")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Delete Role from User - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                    parameter("role-name", "ADMIN")
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.NoContent)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Delete Role from User - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("id", 1)
                    parameter("role-name", "ADMIN")
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.forbidden.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 1)
                    setBody(
                        ChangePasswordRequest(
                            "SecurePass123!",
                            "SecurePass123!",
                            "SecurePass123!",
                        ),
                    )
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.OK)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Change Password - Wrong password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 2)
                    setBody(
                        ChangePasswordRequest(
                            "wrongPassword123!",
                            "SecurePass123!",
                            "SecurePass123!",
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userOrPasswordAreInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.Forbidden)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Password mismatch`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", 2)
                    setBody(
                        ChangePasswordRequest(
                            "SecurePass123!",
                            "SecurePass123!",
                            "notMatchingPass!",
                        ),
                    )
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.passwordMismatch.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }
}
