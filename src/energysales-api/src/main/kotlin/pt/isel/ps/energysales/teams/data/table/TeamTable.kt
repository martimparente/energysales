package pt.isel.ps.energysales.teams.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import pt.isel.ps.energysales.users.data.table.UserTable

object TeamTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val location = reference("location", LocationTable)
    val manager = reference("manager", UserTable).nullable()
}
