package pt.isel.ps.ecoenergy.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import pt.isel.ps.ecoenergy.auth.data.Users
import pt.isel.ps.ecoenergy.team.data.PersonTable
import pt.isel.ps.ecoenergy.team.data.TeamTable

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
            SchemaUtils.drop(TeamTable)
            SchemaUtils.drop(PersonTable)
            SchemaUtils.create(Users)
            SchemaUtils.create(TeamTable)
            SchemaUtils.create(PersonTable)


            for (i in 1..23)
                TeamTable.insert {
                    it[name] = "Team $i"
                    it[location] = "Location $i"
            }
            for (i in 1..23)
                PersonTable.insert {
                    it[name] = "Name $i"
                    it[surname] = "Surname $i"
                    it[email] = "$i@mail.com"
                    it[role] = "Role $i"
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

