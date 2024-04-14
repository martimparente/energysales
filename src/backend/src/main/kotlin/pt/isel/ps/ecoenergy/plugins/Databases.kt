package pt.isel.ps.ecoenergy.plugins

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import pt.isel.ps.ecoenergy.auth.data.repository.Users

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
        val database = Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = user,
            password = password,
        )

        transaction {
            SchemaUtils.create(Users)
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

