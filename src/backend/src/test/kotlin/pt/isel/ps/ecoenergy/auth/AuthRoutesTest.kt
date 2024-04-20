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
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import pt.isel.ps.ecoenergy.Uris
import pt.isel.ps.ecoenergy.auth.data.Users
import pt.isel.ps.ecoenergy.auth.http.model.LoginRequest
import pt.isel.ps.ecoenergy.auth.http.model.LoginResponse
import pt.isel.ps.ecoenergy.auth.http.model.Problem
import pt.isel.ps.ecoenergy.auth.http.model.SignUpRequest
import kotlin.test.Test

class AuthRoutesTest {

    companion object {
        private fun ApplicationTestBuilder.testClient(): HttpClient {
            environment {
                config = ApplicationConfig("application-test.conf")
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

        @JvmStatic
        @BeforeClass
        fun setDatabase(): Unit = testApplication {
            environment {
                config = ApplicationConfig("application-test.conf")
            }
            try {
                Database.connect(
                    url = "jdbc:postgresql://localhost:5434/testing_db",
                    driver = "org.postgresql.Driver",
                    user = "testing_user",
                    password = "testing_password",
                )

                transaction {
                    SchemaUtils.drop(Users)
                    SchemaUtils.create(Users)
                    Users.deleteAll()
                    Users.insert {
                        it[username] = "testUser" // pass = "SecurePass123!"
                        it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
                        it[salt] = "c3f842f3630ebb3d96543709bc316402"
                    }
                }
            } catch (e: Exception) {
                println("Error connecting to the database: ${e.message}")

            }
        }
    }

    @Test
    fun `SignUp - Success`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("newTestUser", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            response.shouldHaveStatus(HttpStatusCode.Created)
            response.shouldHaveContentType(ContentType.Application.Json)
        }
    }

    @Test
    fun `SignUp - Invalid username length`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("123", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userIsInvalid.type)
            response.shouldHaveStatus(HttpStatusCode.BadRequest)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `SignUp - Username already exists`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("testUser", "SecurePass123!", "SecurePass123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userAlreadyExists.type)
            response.shouldHaveStatus(HttpStatusCode.Conflict)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `SignUp - Password mismatch`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("testUser", "SecurePass123!", "PassSecure123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.passwordMismatch.type)
            response.shouldHaveStatus(HttpStatusCode.BadRequest)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `SignUp - Insecure Password`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_SIGNUP) {
            setBody(SignUpRequest("testUser", "insecure", "insecure"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.insecurePassword.type)
            response.shouldHaveStatus(HttpStatusCode.BadRequest)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `Login - Success`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_LOGIN) {
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
    fun `Login - User not found`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_LOGIN) {
            setBody(LoginRequest("nonExistUser", "SecurePass123!"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userOrPasswordAreInvalid.type)
            response.shouldHaveStatus(HttpStatusCode.Forbidden)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }

    @Test
    fun `Login - Password invalid`() = testApplication {
        testClient().post(Uris.API + Uris.AUTH_LOGIN) {
            setBody(LoginRequest("testUser", "wrongPassword"))
        }.also { response ->
            response.body<Problem>().type.shouldBeEqual(Problem.userOrPasswordAreInvalid.type)
            response.shouldHaveStatus(HttpStatusCode.Forbidden)
            response.shouldHaveContentType(ContentType.Application.ProblemJson)
        }
    }
}
