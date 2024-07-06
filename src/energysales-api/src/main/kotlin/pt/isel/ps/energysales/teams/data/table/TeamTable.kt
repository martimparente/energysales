package pt.isel.ps.energysales.teams.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.clients.data.ClientTable
import pt.isel.ps.energysales.services.data.table.ServiceTable
import pt.isel.ps.energysales.users.data.table.UserTable

object TeamTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val location = reference("location", LocationTable)
    val manager = reference("manager", UserTable).nullable()
}

object TeamServices : Table() {
    val team = reference("team", TeamTable)
    val service = reference("service", ServiceTable)
    override val primaryKey = PrimaryKey(team, service)
}

object TeamClients : Table() {
    val team = reference("team", TeamTable)
    val client = reference("client", ClientTable)
    override val primaryKey = PrimaryKey(team, client)
}
