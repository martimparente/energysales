package pt.isel.ps.energysales.auth.data

import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): Int

    suspend fun getUserById(uid: Int): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun getUserRoles(uid: Int): Set<Role>

    suspend fun userExists(username: String): Boolean

    suspend fun updateUser(user: User): Boolean

    suspend fun assignRoleToUser(
        uid: Int,
        roleName: String,
    ): Boolean

    suspend fun deleteRoleFromUser(
        uid: Int,
        roleName: String,
    ): Boolean
}
