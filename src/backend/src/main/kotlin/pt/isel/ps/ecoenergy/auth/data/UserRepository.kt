package pt.isel.ps.ecoenergy.auth.data

import pt.isel.ps.ecoenergy.auth.domain.model.User

interface UserRepository {
    suspend fun createUser(username: String, password: String, salt: String): Int

    suspend fun getUserByUsername(username: String): User?

    suspend fun userExists(username: String): Boolean
}
