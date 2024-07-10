package pt.isel.ps.energysales.clients.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.teams.data.table.LocationTable
import pt.isel.ps.energysales.teams.data.table.TeamTable

object ClientTable : IntIdTable() {
    val name = varchar("name", 50)
    val nif = varchar("nif", 9).uniqueIndex()
    val phone = varchar("phone", 9).uniqueIndex()
    val location = reference("location", LocationTable)
    val team = reference("team", TeamTable)
    val seller = reference("seller", SellerTable).nullable()
}
