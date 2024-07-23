package pt.isel.ps.energysales.users.data

import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.users.data.entity.RoleEntity
import pt.isel.ps.energysales.users.data.entity.UserCredentialsEntity
import pt.isel.ps.energysales.users.data.entity.UserEntity
import pt.isel.ps.energysales.users.data.table.RoleTable
import pt.isel.ps.energysales.users.data.table.UserCredentialsTable
import pt.isel.ps.energysales.users.data.table.UserTable
import pt.isel.ps.energysales.users.domain.Role
import pt.isel.ps.energysales.users.domain.User
import pt.isel.ps.energysales.users.domain.UserCredentials
import pt.isel.ps.energysales.users.domain.toRole

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

    override suspend fun getUserByEmail(email: String): User? =
        dbQuery {
            UserEntity.find { UserTable.email eq email }.singleOrNull()?.toUser()
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

    override suspend fun updateUser(user: User): User? =
        dbQuery {
            UserEntity.findById(user.id)?.let { userEntity ->
                userEntity.name = user.name
                userEntity.surname = user.surname
                userEntity.email = user.email
                userEntity.role = RoleEntity.find { RoleTable.name eq user.role.name }.single()
                userEntity.toUser()
            }
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

    override suspend fun getAll(): List<User> =
        dbQuery {
            UserEntity.all().map { it.toUser() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<User> =
        dbQuery {
            UserEntity
                .find { UserTable.id greater (lastKeySeen ?: 0) }
                .orderBy(UserTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toUser() }
                .toList()
        }

    override suspend fun deleteUser(uid: Int): Boolean =
        dbQuery {
            UserCredentialsEntity.findById(uid)?.delete() ?: false
            UserEntity.findById(uid)?.delete() ?: false
            true
        }
}
