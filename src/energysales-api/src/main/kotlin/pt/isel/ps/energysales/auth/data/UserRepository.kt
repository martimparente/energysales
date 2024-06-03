package pt.isel.ps.energysales.auth.data

import pt.isel.ps.energysales.auth.domain.model.User

interface UserRepository {
    suspend fun createUser(
        username: String,
        password: String,
        salt: String,
    ): Int

    suspend fun getUserById(uid: Int): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun getUserRoles(uid: Int): List<String>

    suspend fun userExists(username: String): Boolean

    suspend fun updateUser(user: User): Boolean

    suspend fun assignRoleToUser(
        uid: Int,
        role: String,
    ): Boolean

    suspend fun deleteRoleFromUser(
        uid: Int,
        role: String,
    ): Boolean
}
