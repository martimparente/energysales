package pt.isel.ps.energysales.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import pt.isel.ps.energysales.auth.data.RoleTable
import pt.isel.ps.energysales.auth.data.UserRoles
import pt.isel.ps.energysales.auth.data.UserTable
import pt.isel.ps.energysales.clients.data.ClientTable
import pt.isel.ps.energysales.products.data.ProductTable
import pt.isel.ps.energysales.sellers.data.PersonTable
import pt.isel.ps.energysales.sellers.data.Role
import pt.isel.ps.energysales.sellers.data.SellerTable
import pt.isel.ps.energysales.teams.data.LocationTable
import pt.isel.ps.energysales.teams.data.TeamTable

// Todo use environment variables to store the database credentials
object DatabaseSingleton {
    // This function is used to run the database queries in a coroutine
    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureDatabases() {
    fun configProperty(propertyName: String) = environment.config.property(propertyName).getString()
    val driverClassName = configProperty("storage.driverClassName")
    val jdbcURL = configProperty("storage.jdbcURL")
    val user = configProperty("storage.user")
    val password = configProperty("storage.password")

    try {
        Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = user,
            password = password,
        )

        transaction {
            log.atInfo().log("Database connected - jdbcURL: $jdbcURL")
            SchemaUtils.drop(SellerTable, TeamTable, PersonTable, UserRoles, RoleTable, UserTable, ProductTable, LocationTable, ClientTable)
            SchemaUtils.create(
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
                it[UserTable.password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
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
            for (i in 1..50) {
                LocationTable.insert {
                    it[district] = "District $i"
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
                    it[price] = (Math.random() * 1000)
                    it[description] = "Description $i"
                    it[image] = "Image $i"
                }

                SellerTable.insert {
                    it[totalSales] = 0.0f
                    it[team] = if (i % 2 == 0) 1 else null
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
    } catch (e: Exception) {
        println("Error connecting to the database: ${e.message}")
    }

    routing {
        /* // Create user
         post("/users") {
             val input = call.receive<User>()
             val userId = userRepository.createUser(input.username, input.password, input.salt)
             call.respond(HttpStatusCode.Created, userId)
         }

         // Read user
         get("/users/{id}") {
             val username = call.parameters["username"] ?: throw IllegalArgumentException("Invalid ID")
             val user = userRepository.getUserByUsername(username)
             if (user != null) {
                 call.respond(HttpStatusCode.OK, user)
             } else {
                 call.respond(HttpStatusCode.NotFound)
             }*/
    }

    /* // Update user
     put("/users/{id}") {
         val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
         val user = call.receive<User>()
         userService.update(id, user)
         call.respond(HttpStatusCode.OK)
     }

     // Delete user
     delete("/users/{id}") {
         val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
         userService.delete(id)
         call.respond(HttpStatusCode.OK)
     }*/
}