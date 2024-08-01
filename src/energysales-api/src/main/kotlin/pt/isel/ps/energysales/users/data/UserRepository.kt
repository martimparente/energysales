package pt.isel.ps.energysales.users.data

import pt.isel.ps.energysales.users.domain.Role
import pt.isel.ps.energysales.users.domain.User
import pt.isel.ps.energysales.users.domain.UserCredentials

interface UserRepository {
    suspend fun createUser(
        user: User,
        userCredentials: UserCredentials,
    ): String

    suspend fun updateUser(user: User): User?

    suspend fun getUserById(uid: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserCredentialsByUsername(username: String): UserCredentials?

    suspend fun getUserCredentialsById(uid: String): UserCredentials?

    suspend fun userExists(username: String): Boolean

    suspend fun isEmailAvailable(email: String): Boolean

    suspend fun updateUserCredentials(credentials: UserCredentials): Boolean

    suspend fun getUserRole(uid: String): Role?

    suspend fun changeUserRole(
        uid: String,
        roleName: String,
    ): Boolean

    suspend fun getAll(): List<User>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: String? = null,
    ): List<User>

    suspend fun deleteUser(uid: String): Boolean
}
