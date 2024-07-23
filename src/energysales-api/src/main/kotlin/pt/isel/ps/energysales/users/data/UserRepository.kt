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

    suspend fun getUserById(uid: Int): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserCredentialsByUsername(username: String): UserCredentials?

    suspend fun getUserCredentialsById(uid: String): UserCredentials?

    suspend fun userExists(username: String): Boolean

    suspend fun isEmailAvailable(email: String): Boolean

    suspend fun updateUserCredentials(credentials: UserCredentials): Boolean

    suspend fun getUserRole(uid: Int): Role?

    suspend fun changeUserRole(
        uid: Int,
        roleName: String,
    ): Boolean

    suspend fun getAll(): List<User>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<User>

    suspend fun deleteUser(uid: Int): Boolean
}
