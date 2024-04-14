package pt.isel.ps.ecoenergy.auth.domain.repository

import pt.isel.ps.ecoenergy.auth.domain.model.User

interface UserRepository {
    suspend fun createUser(
        username: String,
        password: String,
        salt: String,
    ): Int

    suspend fun getUserByUsername(username: String): User?

    suspend fun userExistByUserName(username: String): Boolean
}
