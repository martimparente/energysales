package pt.isel.ps.energysales.users.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val email = varchar("email", 254).uniqueIndex()
    val role = reference("role", RoleTable.name)
}
