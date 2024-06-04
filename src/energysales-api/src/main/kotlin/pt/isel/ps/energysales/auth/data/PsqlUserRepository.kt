package pt.isel.ps.energysales.auth.data

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.User
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery

// Mapping function from ResultRow to User
private fun ResultRow.toUser(roles: Set<Role>) =
    User(
        id = this[UserTable.id],
        username = this[UserTable.username],
        password = this[UserTable.password],
        salt = this[UserTable.salt],
        role = roles,
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
object UserRolesTable : Table() {
    val userId = reference("user_id", UserTable.id)
    val roleId = reference("role_id", RoleTable.id)

    override val primaryKey = PrimaryKey(userId, roleId)
}

class PsqlUserRepository : UserRepository {
    override suspend fun createUser(
        username: String,
        password: String,
        salt: String,
        roles: Set<String>,
    ): Int =
        dbQuery {
            // Check if all roles exist
            val existingRoles =
                RoleTable
                    .select { RoleTable.name inList roles.toList() }
                    .map { it[RoleTable.name] }
                    .toSet()

            if (existingRoles.size != roles.size) {
                throw IllegalArgumentException("One or more roles do not exist")
            }
            // Insert user and get the generated ID
            val userId =
                UserTable.insert {
                    it[UserTable.username] = username
                    it[UserTable.password] = password
                    it[UserTable.salt] = salt
                } get UserTable.id

            // Insert user-role relationships
            roles.forEach { roleName ->
                val roleId =
                    RoleTable
                        .select { RoleTable.name eq roleName }
                        .single()[RoleTable.id]

                UserRolesTable.insert {
                    it[this.userId] = userId
                    it[this.roleId] = roleId
                }
            }
            userId
        }

    override suspend fun getUserById(uid: Int): User? =
        dbQuery {
            val userRow = UserTable.select { UserTable.id eq uid }.singleOrNull() ?: return@dbQuery null
            // If the user exists, get its roles
            val roleRows =
                UserRolesTable
                    .innerJoin(RoleTable)
                    .select { UserRolesTable.userId eq uid }
            // Map the roles to the Role enum
            val roles =
                roleRows
                    .mapNotNull { row ->
                        Role.entries.find { it.name == row[RoleTable.name] }
                    }.toSet()

            userRow.toUser(roles)
        }

    override suspend fun getUserByUsername(username: String): User? =
        dbQuery {
            val userRow = UserTable.select { UserTable.username eq username }.singleOrNull() ?: return@dbQuery null

            // If the user exists, get its roles
            val roleRows =
                UserRolesTable
                    .innerJoin(RoleTable)
                    .select { UserRolesTable.userId eq userRow[UserTable.id] }
            // Map the roles to the Role enum
            val roles =
                roleRows
                    .mapNotNull { row ->
                        Role.entries.find { it.name == row[RoleTable.name] }
                    }.toSet()

            userRow.toUser(roles)
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
        roleName: String,
    ): Boolean =
        dbQuery {
            // Get the role id
            val roleIdFound =
                RoleTable
                    .select { RoleTable.name eq roleName }
                    .singleOrNull()
                    ?.get(RoleTable.id) ?: return@dbQuery false

            // Assign the role to the user
            UserRolesTable
                .insert {
                    it[userId] = uid
                    it[roleId] = roleIdFound
                }.insertedCount > 0
        }

    override suspend fun deleteRoleFromUser(
        uid: Int,
        roleName: String,
    ): Boolean =
        dbQuery {
            // Get the role id
            val roleIdFound =
                RoleTable
                    .select { RoleTable.name eq roleName }
                    .singleOrNull()
                    ?.get(RoleTable.id) ?: return@dbQuery false

            // Delete the role from user
            UserRolesTable.deleteWhere {
                (userId eq uid) and (this.roleId eq roleIdFound)
            } > 0 // Returns true if at least one row was deleted
        }

    override suspend fun getUserRoles(uid: Int): Set<Role> =
        dbQuery {
            (UserRolesTable innerJoin RoleTable)
                .slice(RoleTable.name)
                .select { UserRolesTable.userId eq uid }
                .map { row ->
                    Role.valueOf(row[RoleTable.name])
                }.toSet()
        }
}
