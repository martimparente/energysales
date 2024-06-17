package pt.isel.ps.energysales.auth.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.User
import pt.isel.ps.energysales.auth.domain.model.toRole
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery

object UserTable : IntIdTable() {
    val username = varchar("username", length = 50).uniqueIndex()
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val email = varchar("email", 254).uniqueIndex()
}

open class UserEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
    var salt by UserTable.salt
    var name by UserTable.name
    var surname by UserTable.surname
    var email by UserTable.email
    var roles by RoleEntity via UserRolesTable

    fun toUser() =
        User(
            id.value,
            username,
            password,
            salt,
            name,
            surname,
            email,
            roles.map { toRole(it.name) }.toSet(),
        )
}

object RoleTable : IntIdTable() {
    val name = varchar("name", length = 25).uniqueIndex()
}

open class RoleEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<RoleEntity>(RoleTable)

    var name by RoleTable.name
}

// Exposed table for UserRoles
object UserRolesTable : Table() {
    val userId = reference("user_id", UserTable.id)
    val roleId = reference("role_id", RoleTable.id)

    override val primaryKey = PrimaryKey(userId, roleId)
}

class PsqlUserRepository : UserRepository {
    override suspend fun createUser(user: User): Int =
        dbQuery {
            val rolesFound =
                user.roles.map { role ->
                    RoleEntity.find { RoleTable.name eq role.name }.single()
                }

            val userId =
                UserEntity
                    .new {
                        username = user.username
                        password = user.password
                        salt = user.salt
                        name = user.name
                        surname = user.surname
                        email = user.email
                        roles = SizedCollection(rolesFound)
                    }.id

            userId.value
        }

    override suspend fun getUserById(uid: Int): User? =
        dbQuery {
            UserEntity.findById(uid)?.toUser()
        }

    override suspend fun getUserByUsername(username: String): User? =
        dbQuery {
            UserEntity.find { UserTable.username eq username }.singleOrNull()?.toUser()
        }

    override suspend fun userExists(username: String): Boolean =
        dbQuery {
            UserEntity.find { UserTable.username eq username }.count() > 0
        }

    override suspend fun updateUser(user: User): Boolean =
        dbQuery {
            UserEntity.findById(user.id)?.let { userEntity ->
                userEntity.username = user.username
                userEntity.password = user.password
                userEntity.salt = user.salt
                userEntity.name = user.name
                userEntity.surname = user.surname
                userEntity.email = user.email
                true
            } ?: false
        }

    override suspend fun assignRoleToUser(
        uid: Int,
        roleName: String,
    ): Boolean =
        dbQuery {
            val roleFound = RoleEntity.find { RoleTable.name eq roleName }.single()

            UserEntity.findById(uid)?.let { userEntity ->
                userEntity.roles = SizedCollection(userEntity.roles + roleFound)
                true
            } ?: false
        }

    override suspend fun deleteRoleFromUser(
        uid: Int,
        roleName: String,
    ): Boolean =
        dbQuery {
            UserEntity.findById(uid)?.let { userEntity ->
                userEntity.roles = SizedCollection(userEntity.roles.filter { it.name != roleName })
                true
            } ?: false
        }

    override suspend fun getUserRoles(uid: Int): Set<Role> =
        dbQuery {
            UserEntity
                .findById(uid)
                ?.roles
                ?.map { toRole(it.name) }
                ?.toSet() ?: emptySet()
        }
}
