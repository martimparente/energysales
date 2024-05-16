package pt.isel.ps.salescentral.auth.data

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import pt.isel.ps.salescentral.auth.domain.model.User
import pt.isel.ps.salescentral.plugins.DatabaseSingleton.dbQuery

// Mapping function from ResultRow to User
private fun ResultRow.toUser() =
    User(
        id = this[UserTable.id],
        username = this[UserTable.username],
        password = this[UserTable.password],
        salt = this[UserTable.salt],
    )

// Exposed table for Users
object UserTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", length = 50).uniqueIndex()
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)

    override val primaryKey = PrimaryKey(id)
}

object RoleTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 25).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}

// Exposed table for UserRoles
object UserRoles : Table() {
    val userId = integer("user_id").references(UserTable.id)
    val roleId = integer("role_id").references(RoleTable.id)

    override val primaryKey = PrimaryKey(userId, roleId)
}

class PsqlUserRepository : UserRepository {
    override suspend fun createUser(
        username: String,
        password: String,
        salt: String,
    ): Int =
        dbQuery {
            UserTable.insert {
                it[UserTable.username] = username
                it[UserTable.password] = password
                it[UserTable.salt] = salt
            } get UserTable.id
        }

    override suspend fun getUserById(uid: Int): User? =
        dbQuery {
            UserTable
                .select { UserTable.id eq uid }
                .map(ResultRow::toUser)
                .singleOrNull()
        }

    override suspend fun getUserByUsername(username: String): User? =
        dbQuery {
            UserTable
                .select { UserTable.username eq username }
                .map(ResultRow::toUser)
                .singleOrNull()
        }

    override suspend fun userExists(username: String): Boolean =
        dbQuery {
            UserTable
                .select { UserTable.username eq username }
                .count() > 0
        }

    override suspend fun updateUser(user: User): Boolean =
        dbQuery {
            UserTable
                .update({ UserTable.id eq user.id }) {
                    it[username] = user.username
                    it[password] = user.password
                    it[salt] = user.salt
                } > 0
        }

    override suspend fun assignRoleToUser(
        uid: Int,
        role: String,
    ): Boolean =
        dbQuery {
            // Get the role id
            val roleIdFound =
                RoleTable
                    .select { RoleTable.name eq role }
                    .singleOrNull()
                    ?.get(RoleTable.id) ?: return@dbQuery false

            // Assign the role to the user
            UserRoles
                .insert {
                    it[userId] = uid
                    it[roleId] = roleIdFound
                }.insertedCount > 0
        }

    override suspend fun deleteRoleFromUser(
        uid: Int,
        role: String,
    ): Boolean =
        dbQuery {
            // Get the role id
            val roleIdFound =
                RoleTable
                    .select { RoleTable.name eq role }
                    .singleOrNull()
                    ?.get(RoleTable.id) ?: return@dbQuery false

            // Delete the role from user
            UserRoles.deleteWhere {
                (userId eq uid) and (roleId eq roleIdFound)
            } > 0 // Returns true if at least one row was deleted
        }

    override suspend fun getUserRoles(uid: Int): List<String> =
        dbQuery {
            UserRoles
                .select { UserRoles.userId eq uid }
                .map {
                    RoleTable
                        .select { RoleTable.id eq it[UserRoles.roleId] }
                        .single()
                        .get(RoleTable.name)
                }
        }
}
