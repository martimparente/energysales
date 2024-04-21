package pt.isel.ps.ecoenergy.auth.data

import pt.isel.ps.ecoenergy.auth.domain.model.User

interface UserRepository {
    suspend fun createUser(username: String, password: String, salt: String): Int

    suspend fun getUserById(uid: Int): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun userExists(username: String): Boolean

    suspend fun updatePassword(username: String, password: String, salt: String): Int

    suspend fun assignRoleToUser(uid: Int, role: String): Boolean

    suspend fun deleteRoleFromUser(uid: Int, role: String): Boolean

    suspend fun getUserRoles(uid: Int): List<String>
}
