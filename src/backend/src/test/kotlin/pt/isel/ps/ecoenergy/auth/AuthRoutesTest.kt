package pt.isel.ps.ecoenergy.auth

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import pt.isel.ps.ecoenergy.auth.http.model.SignUpRequest
import pt.isel.ps.ecoenergy.common.Problem
import pt.isel.ps.ecoenergy.common.Uris
import kotlin.test.Test

class AuthRoutesTest {

    private fun ApplicationTestBuilder.testClient(): HttpClient {
        environment {

        }
        return createClient {
            install(ContentNegotiation) {
                json()
            }
            // Every request will have the content type application/json
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }


    @Test
    fun `User Create - Success`() = testApplication {
        testClient().post(Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("newTestUser", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            //TODO JWT.decode(response.body<Token>().token).header
            response.shouldHaveStatus(HttpStatusCode.Created)
            response.shouldHaveContentType(ContentType.Application.Json)
        }
    }

    @Test
    fun `User Create - Invalid username length`() = testApplication {
        testClient().post(Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("123", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
            response.shouldHaveStatus(HttpStatusCode.BadRequest)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `User Create - Username already exists`() = testApplication {
        testClient().post(Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("testUser", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userAlreadyExists.type)
            response.shouldHaveStatus(HttpStatusCode.Conflict)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `User Create - Password dont match`() = testApplication {
        testClient().post(Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("testUser", "SecurePass123!", "PassSecure123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.passwordDontMatch.type)
            response.shouldHaveStatus(HttpStatusCode.BadRequest)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `User Create - Insecure Password`() = testApplication {
        testClient().post(Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("nonExistUser", "insecure", "insecure"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.insecurePassword.type)
            response.shouldHaveStatus(HttpStatusCode.Forbidden)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }
}
