package pt.isel.ps.energysales.auth.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.auth.domain.model.Manager
import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.User
import pt.isel.ps.energysales.auth.domain.model.UserCredentials
import pt.isel.ps.energysales.auth.domain.model.toRole
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery

object UserTable : IntIdTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val email = varchar("email", 254).uniqueIndex()
    val role = reference("role", RoleTable.name)
}

object UserCredentialsTable : IntIdTable() {
    val username = varchar("username", length = 50).uniqueIndex()
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)
}

open class UserEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    var name by UserTable.name
    var surname by UserTable.surname
    var email by UserTable.email
    var role by RoleEntity referencedOn UserTable.role

    fun toUser() =
        User(
            id.value,
            name,
            surname,
            email,
            role.name.toRole(),
        )
}

open class UserCredentialsEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserCredentialsEntity>(UserCredentialsTable)

    var username by UserCredentialsTable.username
    var password by UserCredentialsTable.password
    var salt by UserCredentialsTable.salt

    fun toUserCredentials() =
        UserCredentials(
            id.value,
            username,
            password,
            salt,
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
    override suspend fun createUser(
        user: User,
        userCredentials: UserCredentials,
    ): String =
        dbQuery {
            val userEntity =
                UserEntity.new {
                    name = user.name
                    surname = user.surname
                    email = user.email
                    role = RoleEntity.find { RoleTable.name eq user.role.name }.single()
                }

            UserCredentialsEntity.new(userEntity.id.value) {
                username = userCredentials.username
                password = userCredentials.password
                salt = userCredentials.salt
            }

            userEntity.id.value.toString()
        }

    override suspend fun getUserById(uid: Int): User? =
        dbQuery {
            UserEntity.findById(uid)?.toUser()
        }

    override suspend fun getUserCredentialsByUsername(username: String): UserCredentials? =
        dbQuery {
            UserCredentialsEntity
                .find { UserCredentialsTable.username eq username }
                .singleOrNull()
                ?.let { UserCredentials(it.id.value, it.username, it.password, it.salt) }
        }

    override suspend fun getUserCredentialsById(uid: String): UserCredentials? =
        dbQuery {
            UserCredentialsEntity.findById(uid.toInt())?.toUserCredentials()
        }

    override suspend fun userExists(username: String): Boolean =
        dbQuery {
            UserCredentialsEntity.find { UserCredentialsTable.username eq username }.count() > 0
        }

    override suspend fun isEmailAvailable(email: String): Boolean =
        dbQuery {
            UserEntity.find { UserTable.email eq email }.empty()
        }

    override suspend fun updateUserCredentials(credentials: UserCredentials): Boolean =
        dbQuery {
            UserCredentialsEntity.findById(credentials.id)?.let { userCredentialsEntity ->
                userCredentialsEntity.password = credentials.password
                userCredentialsEntity.salt = credentials.salt
                true
            } ?: false
        }

    override suspend fun updateUser(user: User): Boolean =
        dbQuery {
            UserEntity.findById(user.id)?.let { userEntity ->
                userEntity.name = user.name
                userEntity.surname = user.surname
                userEntity.email = user.email
                true
            } ?: false
        }

    override suspend fun changeUserRole(
        uid: Int,
        roleName: String,
    ): Boolean =
        dbQuery {
            val roleFound = RoleEntity.find { RoleTable.name eq roleName }.single()

            UserEntity.findById(uid)?.let { userEntity ->
                userEntity.role = roleFound
                true
            } ?: false
        }

    override suspend fun getUserRole(uid: Int): Role? =
        dbQuery {
            UserEntity
                .findById(uid)
                ?.role
                ?.name
                ?.toRole()
        }

    override suspend fun getManagerCandidates(): List<Manager> =
        dbQuery {
            UserEntity
                .find { UserTable.role eq RoleEntity.find { RoleTable.name eq "MANAGER" }.first().name }
                .map { Manager(it.id.value, it.name, it.surname, it.email) }
        }
}
