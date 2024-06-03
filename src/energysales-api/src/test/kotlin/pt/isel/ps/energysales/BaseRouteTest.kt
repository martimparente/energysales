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
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import pt.isel.ps.energysales.auth.data.RoleTable
import pt.isel.ps.energysales.auth.data.UserRoles
import pt.isel.ps.energysales.auth.data.UserTable
import pt.isel.ps.energysales.auth.http.model.LoginRequest
import pt.isel.ps.energysales.auth.http.model.LoginResponse
import pt.isel.ps.energysales.clients.data.ClientTable
import pt.isel.ps.energysales.products.data.ProductTable
import pt.isel.ps.energysales.sellers.data.PersonTable
import pt.isel.ps.energysales.sellers.data.Role
import pt.isel.ps.energysales.sellers.data.SellerTable
import pt.isel.ps.energysales.teams.data.LocationTable
import pt.isel.ps.energysales.teams.data.TeamTable

open class BaseRouteTest {
    companion object {
        lateinit var token: String

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
                        SchemaUtils.drop(
                            SellerTable,
                            TeamTable,
                            PersonTable,
                            UserRoles,
                            RoleTable,
                            UserTable,
                            ProductTable,
                            LocationTable,
                            ClientTable,
                        )
                        SchemaUtils
                            .create(
                                UserTable,
                                RoleTable,
                                UserRoles,
                                TeamTable,
                                PersonTable,
                                SellerTable,
                                ProductTable,
                                LocationTable,
                                ClientTable,
                            )

                        UserTable.insert {
                            it[username] = "testUser" // pass = "SecurePass123!"
                            it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
                            it[salt] = "c3f842f3630ebb3d96543709bc316402"
                        }
                        RoleTable.insert {
                            it[name] = "admin"
                        }
                        RoleTable.insert {
                            it[name] = "seller"
                        }
                        UserRoles.insert {
                            it[userId] = 1
                            it[roleId] = 2
                        }
                        for (i in 1..3) {
                            LocationTable.insert {
                                it[district] = "Location $i"
                            }
                            TeamTable.insert {
                                it[name] = "Team $i"
                                it[location] = i
                            }
                            PersonTable.insert {
                                it[name] = "Name $i"
                                it[surname] = "Surname $i"
                                it[email] = "$i@mail.com"
                                it[role] = Role.SELLER
                            }
                            ProductTable.insert {
                                it[name] = "Product $i"
                                it[price] = 0.0
                                it[description] = "Description $i"
                            }
                            SellerTable.insert {
                                it[id] = i
                                it[totalSales] = 0.0f
                                it[person] = i
                            }
                            ClientTable.insert {
                                it[name] = "Client $i"
                                // random number of exactly 9 digits
                                it[nif] = (100000000 + (Math.random() * 900000000).toInt()).toString()
                                it[phone] = (100000000 + (Math.random() * 900000000).toInt()).toString()
                                it[location] = i
                            }
                        }
                    }
                    // Login to get the JWT token for testing authenticated routes
                    testClient()
                        .post(Uris.API + Uris.AUTH_LOGIN) {
                            setBody(LoginRequest("testUser", "SecurePass123!"))
                        }.also { response ->
                            token = response.body<LoginResponse>().token
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
                SchemaUtils.drop(
                    SellerTable,
                    TeamTable,
                    PersonTable,
                    UserRoles,
                    RoleTable,
                    UserTable,
                    ProductTable,
                    LocationTable,
                    ClientTable,
                )
            }
        }
    }
}
