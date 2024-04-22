package pt.isel.ps.ecoenergy.auth

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.ecoenergy.BaseRouteTest
import pt.isel.ps.ecoenergy.Uris
import pt.isel.ps.ecoenergy.auth.http.model.ChangePasswordRequest
import pt.isel.ps.ecoenergy.auth.http.model.LoginRequest
import pt.isel.ps.ecoenergy.auth.http.model.LoginResponse
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.RoleRequest
import pt.isel.ps.ecoenergy.auth.http.model.SignUpRequest
import kotlin.test.Test

class AuthRoutesTest : BaseRouteTest() {
    @Test
    fun `SignUp - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    setBody(SignUpRequest("newTestUser", "SecurePass123!", "SecurePass123!"))
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Created)
                    response.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `SignUp - Invalid username length`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    setBody(SignUpRequest("123", "SecurePass123!", "SecurePass123!"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `SignUp - Username already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    setBody(SignUpRequest("testUser", "SecurePass123!", "SecurePass123!"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.userAlreadyExists.type)
                    response.shouldHaveStatus(HttpStatusCode.Conflict)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `SignUp - Password mismatch`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    setBody(SignUpRequest("testUser", "SecurePass123!", "PassSecure123!"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.passwordMismatch.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `SignUp - Insecure Password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_SIGNUP) {
                    setBody(SignUpRequest("testUser", "insecure", "insecure"))
                }.also { response ->
                    response.body<Problem>().type.shouldBeEqual(Problem.insecurePassword.type)
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Login - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("testUser", "SecurePass123!"))
                }.also { response ->
                    response.body<LoginResponse>().tokenType.shouldBeEqual("Bearer")
                    val a = response.body<LoginResponse>().token
                    println(a)
                    response.body<LoginResponse>().expiresIn.shouldBeEqual(3600000)
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
                    headers.append("Authorization", "Bearer $token")
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
                    headers.append("Authorization", "Bearer $token")
                    parameter("id", 1)
                    setBody(RoleRequest("admin"))
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.Created)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Delete Role from User - Success`() =
        testApplication {
            testClient()
                .delete(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $token")
                    parameter("id", 1)
                    parameter("role-id", "seller")
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.NoContent)
                    it.shouldHaveContentType(ContentType.Application.Json)
                }
        }

    @Test
    fun `Change Password - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $token")
                    setBody(
                        ChangePasswordRequest(
                            2,
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
}
