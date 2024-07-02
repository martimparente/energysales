package pt.isel.ps.energysales

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.impl.instantiation.AbstractInstantiator.Companion.log
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import pt.isel.ps.energysales.auth.http.model.LoginRequest
import pt.isel.ps.energysales.auth.http.model.LoginResponse

open class BaseRouteTest {
    companion object {
        lateinit var adminToken: String
        lateinit var sellerToken: String

        // Function to create a test http client
        fun ApplicationTestBuilder.testClient(): HttpClient {
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
        fun beforeTest() {
            log.info { "Before test" }
            testApplication {
                // Load the application-test.conf file to use the test database
                environment {
                    config = ApplicationConfig("application-test.conf")
                }
                // Connect to the test database
                try {
                    Database.connect(
                        url = "jdbc:postgresql://localhost:5434/testing_db",
                        driver = "org.postgresql.Driver",
                        user = "testing_user",
                        password = "testing_password",
                    )
                    // Create the tables and insert the data needed for the tests
                    transaction {
                        fillDb()
                    }
                    // Login to get the JWT token for testing authenticated routes
                    val testClient = testClient()
                    testClient
                        .post(Uris.API + Uris.AUTH_LOGIN) {
                            setBody(LoginRequest("adminUser", "SecurePass123!"))
                        }.also { response ->
                            adminToken = response.body<LoginResponse>().token
                        }
                    testClient
                        .post(Uris.API + Uris.AUTH_LOGIN) {
                            setBody(LoginRequest("sellerUser", "SecurePass123!"))
                        }.also { response ->
                            sellerToken = response.body<LoginResponse>().token
                        }
                } catch (e: Exception) {
                    Assert.fail("Error connecting to the database: ${e.message}")
                }
            }
            log.info { "Test are ready to start" }
        }

        @JvmStatic
        @AfterClass
        fun afterTest() {
            log.info { "After test" }
            transaction {
                dropDb()
            }
        }
    }
}
