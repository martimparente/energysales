package pt.isel.ps.energysales.users.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object RoleTable : IntIdTable() {
    val name = varchar("name", length = 25).uniqueIndex()
}
