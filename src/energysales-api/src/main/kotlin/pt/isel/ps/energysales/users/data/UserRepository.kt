package pt.isel.ps.energysales.users.data

import pt.isel.ps.energysales.users.domain.model.Role
import pt.isel.ps.energysales.users.domain.model.User
import pt.isel.ps.energysales.users.domain.model.UserCredentials

interface UserRepository {
    suspend fun createUser(
        user: User,
        userCredentials: UserCredentials,
    ): String

    suspend fun updateUser(user: User): Boolean

    suspend fun getUserById(uid: Int): User?

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

    suspend fun getManagerCandidates(): List<User>

    suspend fun getAll(): List<User>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<User>
}
