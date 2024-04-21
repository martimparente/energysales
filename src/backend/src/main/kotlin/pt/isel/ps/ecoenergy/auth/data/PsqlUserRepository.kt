package pt.isel.ps.ecoenergy.auth.data

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import pt.isel.ps.ecoenergy.auth.domain.model.User
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery

// Mapping function from ResultRow to User
private fun ResultRow.toUser() = User(
    id = this[Users.id],
    username = this[Users.username],
    password = this[Users.password],
    salt = this[Users.salt],
)

// Exposed table for Users
object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", length = 50).uniqueIndex()
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)

    override val primaryKey = PrimaryKey(id)
}

object Roles : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 25).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}

// Exposed table for UserRoles
object UserRoles : Table() {
    val userId = integer("user_id").references(Users.id)
    val roleId = integer("role_id").references(Roles.id)

    override val primaryKey = PrimaryKey(userId, roleId)
}


class PsqlUserRepository : UserRepository {
    override suspend fun createUser(
        username: String,
        password: String,
        salt: String,
    ): Int = dbQuery {
        Users.insert {
            it[Users.username] = username
            it[Users.password] = password
            it[Users.salt] = salt
        } get Users.id
    }

    override suspend fun getUserById(uid: Int): User? = dbQuery {
        Users
            .select { Users.id eq uid }
            .map(ResultRow::toUser)
            .singleOrNull()
    }

    override suspend fun getUserByUsername(username: String): User? = dbQuery {
        Users
            .select { Users.username eq username }
            .map(ResultRow::toUser)
            .singleOrNull()
    }

    override suspend fun userExists(username: String): Boolean = dbQuery {
        Users
            .select { Users.username eq username }
            .count() > 0
    }

    override suspend fun updatePassword(username: String, password: String, salt: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun assignRoleToUser(uid: Int, role: String): Boolean = dbQuery {
        // Get the role id
        val roleIdFound = Roles
            .select { Roles.name eq role }
            .singleOrNull()
            ?.get(Roles.id) ?: return@dbQuery false

        // Assign the role to the user
        UserRoles.insert {
            it[userId] = uid
            it[roleId] = roleIdFound
        }.insertedCount > 0
    }

    override suspend fun deleteRoleFromUser(uid: Int, role: String): Boolean = dbQuery {
        // Get the role id
        val roleIdFound = Roles
            .select { Roles.name eq role }
            .singleOrNull()
            ?.get(Roles.id) ?: return@dbQuery false

        // Delete the role from user
        UserRoles.deleteWhere {
            (userId eq uid) and (roleId eq roleIdFound)
        } > 0 // Returns true if at least one row was deleted

    }


    override suspend fun getUserRoles(uid: Int): List<String> = dbQuery {
        UserRoles
            .select { UserRoles.userId eq uid }
            .map {
                Roles
                    .select { Roles.id eq it[UserRoles.roleId] }
                    .single()
                    .get(Roles.name)
            }
    }
}
