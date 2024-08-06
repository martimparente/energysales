package pt.isel.ps.energysales.partners.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.clients.data.table.ClientTable
import pt.isel.ps.energysales.services.data.table.ServiceTable
import pt.isel.ps.energysales.users.data.table.UserTable

object PartnerTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val location = reference("location", LocationTable)
    val manager = reference("manager", UserTable).nullable()
    val avatarPath = varchar("avatar_path", 255).nullable() // New column for avatar URL
}

object PartnerServices : Table() {
    val partner = reference("partner", PartnerTable)
    val service = reference("service", ServiceTable)
    override val primaryKey = PrimaryKey(partner, service)
}

object PartnerClients : Table() {
    val partner = reference("partner", PartnerTable)
    val client = reference("client", ClientTable)
    override val primaryKey = PrimaryKey(partner, client)
}
