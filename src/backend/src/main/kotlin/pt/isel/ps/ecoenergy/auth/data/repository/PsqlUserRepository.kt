package pt.isel.ps.ecoenergy.auth.data.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import pt.isel.ps.ecoenergy.auth.domain.model.User
import pt.isel.ps.ecoenergy.auth.domain.repository.UserRepository
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery

private fun ResultRow.toUser(): User =
    User(
        id = this[Users.id],
        username = this[Users.username],
        password = this[Users.password],
        salt = this[Users.salt],
    )

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", length = 50)
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)

    override val primaryKey = PrimaryKey(id)
}

class PsqlUserRepository(
    private val db: Database?,
) : UserRepository {
    override suspend fun createUser(
        username: String,
        password: String,
        salt: String,
    ): Int =
        dbQuery {
            Users.insert {
                it[Users.username] = username
                it[Users.password] = password
                it[Users.salt] = salt
            } get Users.id
        }

    override suspend fun getUserByUsername(username: String): User? =
        dbQuery {
            Users
                .select { Users.username eq username }
                .map(ResultRow::toUser)
                .singleOrNull()
        }

    override suspend fun userExistByUserName(username: String): Boolean =
        dbQuery {
            Users
                .select { Users.username eq username }
                .count() > 0
        }
}
