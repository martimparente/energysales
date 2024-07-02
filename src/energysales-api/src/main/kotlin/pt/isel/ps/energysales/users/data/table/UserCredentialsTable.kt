package pt.isel.ps.energysales.users.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object UserCredentialsTable : IntIdTable() {
    val username = varchar("username", length = 50).uniqueIndex()
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)
}
